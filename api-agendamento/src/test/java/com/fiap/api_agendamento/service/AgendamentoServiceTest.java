package com.fiap.api_agendamento.service;

import com.fiap.api_agendamento.domain.Agendamento;
import com.fiap.api_agendamento.domain.StatusAgendamento;
import com.fiap.api_agendamento.domain.TipoUsuario;
import com.fiap.api_agendamento.domain.Usuario;
import com.fiap.api_agendamento.dto.EditarConsultaInput;
import com.fiap.api_agendamento.dto.RegistrarConsultaInput;
import com.fiap.api_agendamento.exception.ConsultaHorarioIndisponiveException;
import com.fiap.api_agendamento.repository.AgendamentoRepository;
import com.fiap.api_agendamento.repository.UsuarioRepository;
import com.fiap.api_agendamento.service.event.ConsultaEvento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioAutenticadoService usuarioAutenticadoService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    private AgendamentoService agendamentoService;

    private Usuario paciente;
    private Usuario medico;

    @BeforeEach
    void setUp() {
        agendamentoService = new AgendamentoService(
                agendamentoRepository,
                usuarioRepository,
                usuarioAutenticadoService,
                applicationEventPublisher
        );

        paciente = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Maria Paciente")
                .email("maria@teste.com")
                .tipo(TipoUsuario.PACIENTE)
                .build();

        medico = Usuario.builder()
                .id(UUID.randomUUID())
                .nome("Dr. João")
                .email("joao@teste.com")
                .tipo(TipoUsuario.MEDICO)
                .build();
    }

    @Test
    void registrarConsulta_devePublicarEventoComTipoCriadaEDadosDeUsuarios() {
        RegistrarConsultaInput input = new RegistrarConsultaInput(
                paciente.getId(),
                medico.getId(),
                OffsetDateTime.now().plusDays(1),
                "Cardiologia",
                "Primeira consulta"
        );

        when(agendamentoRepository.findByMedicoIdAndDataHoraAndStatus(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(medico.getId())).thenReturn(Optional.of(medico));

        agendamentoService.registrarConsulta(input);

        ArgumentCaptor<ConsultaEvento> captor = ArgumentCaptor.forClass(ConsultaEvento.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());

        ConsultaEvento evento = captor.getValue();
        assertThat(evento.eventId()).isNotNull();
        assertThat(evento.tipoEvento()).isEqualTo(ConsultaEvento.TIPO_CRIADA);
        assertThat(evento.pacienteNome()).isEqualTo("Maria Paciente");
        assertThat(evento.pacienteEmail()).isEqualTo("maria@teste.com");
        assertThat(evento.medicoNome()).isEqualTo("Dr. João");
        assertThat(evento.medicoEmail()).isEqualTo("joao@teste.com");
        assertThat(evento.status()).isEqualTo(StatusAgendamento.AGENDADA.name());
        assertThat(evento.especialidade()).isEqualTo("Cardiologia");
        assertThat(evento.timestamp()).isNotNull();
    }

    @Test
    void editarConsulta_devePublicarEventoComTipoAtualizada() {
        Agendamento agendamentoExistente = Agendamento.builder()
                .id(UUID.randomUUID())
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .dataHora(OffsetDateTime.now().plusDays(2))
                .status(StatusAgendamento.AGENDADA)
                .especialidade("Cardiologia")
                .build();

        EditarConsultaInput input = new EditarConsultaInput(null, null, "Dermatologia", null);

        when(agendamentoRepository.findById(agendamentoExistente.getId()))
                .thenReturn(Optional.of(agendamentoExistente));
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(medico.getId())).thenReturn(Optional.of(medico));

        agendamentoService.editarConsulta(agendamentoExistente.getId(), input);

        ArgumentCaptor<ConsultaEvento> captor = ArgumentCaptor.forClass(ConsultaEvento.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().tipoEvento()).isEqualTo(ConsultaEvento.TIPO_ATUALIZADA);
        assertThat(captor.getValue().especialidade()).isEqualTo("Dermatologia");
    }

    @Test
    void cancelarConsulta_devePublicarEventoComTipoAtualizada() {
        Agendamento agendamentoExistente = Agendamento.builder()
                .id(UUID.randomUUID())
                .pacienteId(paciente.getId())
                .medicoId(medico.getId())
                .dataHora(OffsetDateTime.now().plusDays(2))
                .status(StatusAgendamento.AGENDADA)
                .especialidade("Cardiologia")
                .build();

        when(agendamentoRepository.findById(agendamentoExistente.getId()))
                .thenReturn(Optional.of(agendamentoExistente));
        when(agendamentoRepository.save(any(Agendamento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioRepository.findById(paciente.getId())).thenReturn(Optional.of(paciente));
        when(usuarioRepository.findById(medico.getId())).thenReturn(Optional.of(medico));

        agendamentoService.cancelarConsulta(agendamentoExistente.getId());

        ArgumentCaptor<ConsultaEvento> captor = ArgumentCaptor.forClass(ConsultaEvento.class);
        verify(applicationEventPublisher).publishEvent(captor.capture());

        assertThat(captor.getValue().tipoEvento()).isEqualTo(ConsultaEvento.TIPO_ATUALIZADA);
        assertThat(captor.getValue().status()).isEqualTo(StatusAgendamento.CANCELADA.name());
    }

    @Test
    void registrarConsulta_quandoHorarioIndisponivel_naoDevePublicarEvento() {
        RegistrarConsultaInput input = new RegistrarConsultaInput(
                paciente.getId(),
                medico.getId(),
                OffsetDateTime.now().plusDays(1),
                "Cardiologia",
                null
        );

        Agendamento conflitante = Agendamento.builder()
                .id(UUID.randomUUID())
                .pacienteId(UUID.randomUUID())
                .medicoId(medico.getId())
                .dataHora(input.dataHora())
                .status(StatusAgendamento.AGENDADA)
                .especialidade("Cardiologia")
                .build();

        when(agendamentoRepository.findByMedicoIdAndDataHoraAndStatus(any(), any(), any()))
                .thenReturn(Optional.of(conflitante));

        assertThrows(
                ConsultaHorarioIndisponiveException.class,
                () -> agendamentoService.registrarConsulta(input)
        );

        verify(applicationEventPublisher, never()).publishEvent(any());
    }
}
