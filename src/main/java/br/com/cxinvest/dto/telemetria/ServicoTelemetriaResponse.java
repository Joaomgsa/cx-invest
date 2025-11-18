package br.com.cxinvest.dto.telemetria;

public record ServicoTelemetriaResponse(
        String nome,
        int quantidadeChamadas,
        int mediaTempoRespostaMs
) {
}

