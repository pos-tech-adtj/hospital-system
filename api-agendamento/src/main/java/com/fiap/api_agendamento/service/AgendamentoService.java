package com.fiap.api_agendamento.service;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;
import com.fiap.api_agendamento.domain.TipoUsuario;
import com.fiap.api_agendamento.domain.Usuario;
import com.fiap.api_agendamento.dto.EditarConsultaInput;
import com.fiap.api_agendamento.dto.HistoricoConsultaFiltro;
import com.fiap.api_agendamento.dto.RegistrarConsultaInput;
import com.fiap.api_agendamento.exception.ConsultaDataPassadaException;
import com.fiap.api_agendamento.exception.ConsultaHorarioIndisponiveException;
import com.fiap.api_agendamento.exception.ConsultaNaoAgendadaException;
import com.fiap.api_agendamento.exception.ConsultaNaoEncontradaException;
import com.fiap.api_agendamento.repository.AgendamentoRepository;
import com.fiap.api_agendamento.repository.UsuarioRepository;
import com.fiap.api_agendamento.security.UsuarioPrincipal;
import com.fiap.api_agendamento.service.event.ConsultaEvento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            UsuarioRepository usuarioRepository,
            UsuarioAutenticadoService usuarioAutenticadoService,
            ApplicationEventPublisher applicationEventPublisher
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<Agendamento> listarPorPaciente(UUID idPaciente) {
        UsuarioPrincipal principal = usuarioAutenticadoService.obterPrincipal();

        if (principal.getTipo() == TipoUsuario.PACIENTE) {
            if (!principal.getId().equals(idPaciente)) {
                throw new AccessDeniedException("Paciente não pode acessar consultas de outro paciente");
            }

            // Aplica filtro no banco usando o próprio ID do paciente autenticado.
            return agendamentoRepository.findByPacienteIdOrderByDataHoraDesc(principal.getId());
        }

        return agendamentoRepository.findByPacienteIdOrderByDataHoraDesc(idPaciente);
    }

    public List<Agendamento> listarHistoricoConsultas(HistoricoConsultaFiltro filtro) {
        UsuarioPrincipal principal = usuarioAutenticadoService.obterPrincipal();

        return agendamentoRepository.findAll(
                especificacao(filtro, principal),
                Sort.by(Sort.Direction.DESC, "dataHora")
        );
    }

    @Transactional
    public Agendamento registrarConsulta(RegistrarConsultaInput input) {
        // Verifica se a data e hora da consulta é futura
        validaDataHoraFutura(input.dataHora());

        // Verifica se já existe um agendamento para o mesmo médico e horário
        verificarDuplicidadeAgendamento(input.medicoId(), input.dataHora());

        Agendamento novoAgendamento = Agendamento.builder()
                .pacienteId(input.pacienteId())
                .medicoId(input.medicoId())
                .dataHora(input.dataHora())
                .status(StatusAgendamento.AGENDADA)
                .especialidade(input.especialidade())
                .observacoes(input.observacoes())
                .build();

        Agendamento agendamentoSalvo = agendamentoRepository.save(novoAgendamento);

        publicarEventoConsulta(agendamentoSalvo, ConsultaEvento.TIPO_CRIADA);

        return agendamentoSalvo;
    }

    @Transactional
    public Agendamento editarConsulta(
            UUID id,
            EditarConsultaInput input
    ) {
        Agendamento agendamento = buscarConsulta(id);

        validarConsultaAgendada(agendamento);

        if (input.dataHora() != null && !input.dataHora().isEqual(agendamento.getDataHora())) {
            validaDataHoraFutura(input.dataHora());
            verificarDuplicidadeAgendamento(agendamento.getMedicoId(), input.dataHora());
            agendamento.setDataHora(input.dataHora());
        }

        if (input.status() != null) {
            agendamento.setStatus(input.status());
        }

        if (input.especialidade() != null) {
            agendamento.setEspecialidade(input.especialidade());
        }

        if (input.observacoes() != null) {
            agendamento.setObservacoes(input.observacoes());
        }

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        publicarEventoConsulta(agendamentoSalvo, ConsultaEvento.TIPO_ATUALIZADA);

        return agendamentoSalvo;
    }

    private Specification<Agendamento> especificacao(HistoricoConsultaFiltro filtro, UsuarioPrincipal principal) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (principal.getTipo() == TipoUsuario.PACIENTE) {
                predicates.add(cb.equal(root.get("pacienteId"), principal.getId()));
            }

            if (filtro != null) {
                if (filtro.status() != null) {
                    predicates.add(cb.equal(root.get("status"), filtro.status()));
                }
                if (Boolean.TRUE.equals(filtro.apenasFuturas())) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("dataHora"), OffsetDateTime.now()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void validaDataHoraFutura(OffsetDateTime dataHora) {
        if (dataHora.isBefore(OffsetDateTime.now())) {
            throw new ConsultaDataPassadaException(dataHora);
        }
    }

    private void verificarDuplicidadeAgendamento(UUID medicoId, OffsetDateTime dataHora) {
        Optional<Agendamento> agendamentoExistente = agendamentoRepository.findByMedicoIdAndDataHoraAndStatus(medicoId, dataHora, StatusAgendamento.AGENDADA);

        if (agendamentoExistente.isPresent()) {
            throw new ConsultaHorarioIndisponiveException(medicoId, dataHora);
        }
    }

    private void validarConsultaAgendada(Agendamento agendamento) {
        if (agendamento.getStatus() != StatusAgendamento.AGENDADA) {
            throw new ConsultaNaoAgendadaException(agendamento.getId(), agendamento.getStatus());
        }
    }

    private Agendamento buscarConsulta(UUID id) {
        return agendamentoRepository.findById(id).orElseThrow(() -> new ConsultaNaoEncontradaException(id));
    }

    @Transactional
    public Agendamento cancelarConsulta(UUID id) {
        Agendamento agendamento = buscarConsulta(id);

        // Cancelamento só é permitido para consultas AGENDADAS
        validarConsultaAgendada(agendamento);
        agendamento.setStatus(StatusAgendamento.CANCELADA);

        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        publicarEventoConsulta(agendamentoSalvo, ConsultaEvento.TIPO_ATUALIZADA);

        return agendamentoSalvo;
    }

    // Envio ao RabbitMQ só ocorre após o commit, via ConsultaEventoListener (AFTER_COMMIT).
    private void publicarEventoConsulta(Agendamento agendamento, String tipoEvento) {
        Usuario paciente = usuarioRepository.findById(agendamento.getPacienteId()).orElse(null);
        Usuario medico = usuarioRepository.findById(agendamento.getMedicoId()).orElse(null);

        ConsultaEvento evento = new ConsultaEvento(
                UUID.randomUUID(),
                tipoEvento,
                agendamento.getId(),
                agendamento.getPacienteId(),
                paciente != null ? paciente.getNome() : null,
                paciente != null ? paciente.getEmail() : null,
                agendamento.getMedicoId(),
                medico != null ? medico.getNome() : null,
                medico != null ? medico.getEmail() : null,
                agendamento.getDataHora(),
                agendamento.getEspecialidade(),
                agendamento.getObservacoes(),
                agendamento.getStatus().name(),
                OffsetDateTime.now()
        );

        applicationEventPublisher.publishEvent(evento);
    }

}