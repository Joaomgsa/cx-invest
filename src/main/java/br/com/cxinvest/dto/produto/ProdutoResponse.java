package br.com.cxinvest.dto.produto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidadeMensal,
        Long perfilId,
        String perfilNome
) {
}
