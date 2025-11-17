package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;

/**
 * DTO de retorno da simulação contendo resumo do resultado e referências ao produto/cliente.
 */
public record SimulacaoResponse(
        Long produtoId,
        String produtoNome,
        Long clienteId,
        java.math.BigDecimal valorSimulacao,
        java.math.BigDecimal valorFinal,
        Integer prazoMeses,
        java.math.BigDecimal rentabilidadeEfetiva
) {
}
