package com.fiap.api_agendamento.service;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;
import com.fiap.api_agendamento.domain.TipoUsuario;
import com.fiap.api_agendamento.repository.AgendamentoRepository;
import com.fiap.api_agendamento.security.UsuarioPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            UsuarioAutenticadoService usuarioAutenticadoService
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
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

    public List<Agendamento> listarHistoricoConsultas() {
        UsuarioPrincipal principal = usuarioAutenticadoService.obterPrincipal();

        if (principal.getTipo() == TipoUsuario.PACIENTE) {
            return agendamentoRepository.findByPacienteIdOrderByDataHoraDesc(principal.getId());
        }

        return agendamentoRepository.findAllByOrderByDataHoraDesc();
    }

    public Agendamento registrarConsulta(
            UUID pacienteId,
            UUID medicoId,
            LocalDateTime dataHora,
            String especialidade,
            String observacoes
    ) {
        usuarioAutenticadoService.obterPrincipal();

        Agendamento novoAgendamento = Agendamento.builder()
                .pacienteId(pacienteId)
                .medicoId(medicoId)
                .dataHora(dataHora)
                .status(StatusAgendamento.AGENDADA)
                .especialidade(especialidade)
                .observacoes(observacoes)
                .build();

        return agendamentoRepository.save(novoAgendamento);
    }

    public Agendamento editarConsulta(
            UUID id,
            LocalDateTime dataHora,
            StatusAgendamento status,
            String especialidade,
            String observacoes
    ) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        if (dataHora != null) {
            agendamento.setDataHora(dataHora);
        }

        if (status != null) {
            agendamento.setStatus(status);
        }

        if (especialidade != null) {
            agendamento.setEspecialidade(especialidade);
        }

        if (observacoes != null) {
            agendamento.setObservacoes(observacoes);
        }

        return agendamentoRepository.save(agendamento);
    }
}