package com.fiap.api_agendamento.service;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.TipoUsuario;
import com.fiap.api_agendamento.repository.AgendamentoRepository;
import com.fiap.api_agendamento.security.UsuarioPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
            return agendamentoRepository.findByPacienteId(principal.getId());
        }

        return agendamentoRepository.findByPacienteId(idPaciente);
    }
}