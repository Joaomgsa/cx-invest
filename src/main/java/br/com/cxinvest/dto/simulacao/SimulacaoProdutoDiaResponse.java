package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de agregação diária por produto para relatório de simulações.
 */
public record SimulacaoProdutoDiaResponse(
        String produtoNome,
        LocalDate data,
        Long totalSimulacoes,
        BigDecimal mediaValorFinal
) {
}

