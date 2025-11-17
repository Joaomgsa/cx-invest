package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de resumo diário por produto para a funcionalidade de simulação.
 * Campos:
 * - produtoNome: nome do produto
 * - data: dia referente ao resumo
 * - totalSimulacoes: total de simulações realizadas nesse dia
 * - mediaValorFinal: média do valor final obtido nas simulações desse dia
 */
public record SimulacaoProdutoDiaResponse(
        String produtoNome,
        LocalDate data,
        Long totalSimulacoes,
        BigDecimal mediaValorFinal
) {
}
