package br.com.cxinvest.dto.telemetria;

import java.util.List;

public record TelemetriaResponse(
        List<ServicoTelemetriaResponse> servicos,
        PeriodoTelemetriaResponse periodo
) {}

