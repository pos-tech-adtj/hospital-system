package com.fiap.api_agendamento.scheduler;

import com.fiap.api_agendamento.service.AgendamentoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LembreteConsultaScheduler {

    private final AgendamentoService agendamentoService;

    public LembreteConsultaScheduler(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Scheduled(fixedDelayString = "${agendamento.lembrete.intervalo-ms}")
    public void executar() {
        agendamentoService.enviarLembretesConsultasProximas();
    }
}
