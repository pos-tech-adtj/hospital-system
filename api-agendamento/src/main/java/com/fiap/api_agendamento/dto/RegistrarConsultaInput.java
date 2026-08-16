package com.fiap.api_agendamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegistrarConsultaInput(
    @NotNull(message = "ID do paciente é obrigatório")
    UUID pacienteId,
    @NotNull(message = "ID do médico é obrigatório")
    UUID medicoId,
    @NotNull(message = "Data e hora da consulta é obrigatória")
    OffsetDateTime dataHora,
    @NotBlank(message = "Especialidade é obrigatória e não pode estar vazia")
    String especialidade,
    String observacoes
) {
}

