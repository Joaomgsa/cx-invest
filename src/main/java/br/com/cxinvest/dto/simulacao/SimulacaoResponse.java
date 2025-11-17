package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO de retorno da simulação contendo resumo do resultado e referências ao produto/cliente.
 */
public record SimulacaoResponse(
        SimulacaoProdutoResponse produto,
        SimulacaoResultadoResponse resultado,
        Timestamp dataSimulacao
) {
}
