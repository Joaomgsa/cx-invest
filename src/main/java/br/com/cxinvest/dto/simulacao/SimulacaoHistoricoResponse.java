package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO resumo de uma simulação para uso em histórico/paginação.
 */
public record SimulacaoHistoricoResponse(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        String dataSimulacao
) {
}
