package com.fiap.api_agendamento.scheduler;

import com.fiap.api_agendamento.service.AgendamentoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CancelamentoConsultaScheduler {

    private final AgendamentoService agendamentoService;

    public CancelamentoConsultaScheduler(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @Scheduled(fixedDelayString = "${agendamento.cancelamento.intervalo-ms}")
    public void executar() {
        agendamentoService.cancelarConsultasExpiradas();
    }
}

