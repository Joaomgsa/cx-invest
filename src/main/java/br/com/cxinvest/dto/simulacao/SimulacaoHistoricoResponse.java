package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO resumo de uma simulação para uso em histórico/paginação.
 */
public record SimulacaoHistoricoResponse(
        Long id,
        Long produtoId,
        String produtoNome,
        Long clienteId,
        BigDecimal valorSimulacao,
        BigDecimal valorFinal,
        Integer prazoMeses,
        BigDecimal rentabilidadeEfetiva,
        String dataSimulacao
) {
}
