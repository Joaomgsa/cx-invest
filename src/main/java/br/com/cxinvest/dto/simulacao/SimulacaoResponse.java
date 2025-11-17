package br.com.cxinvest.dto.simulacao;

/**
 * DTO de retorno da simulação contendo resumo do resultado e referências ao produto/cliente.
 * dataSimulacao agora é uma String ISO-8601 (Instant.toString()).
 */
public record SimulacaoResponse(
        SimulacaoProdutoResponse produtoValidado,
        SimulacaoResultadoResponse resultadoSimulacao,
        String dataSimulacao
) {
}
