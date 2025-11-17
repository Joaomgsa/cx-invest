package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;

public record SimulacaoProdutoResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {
}

