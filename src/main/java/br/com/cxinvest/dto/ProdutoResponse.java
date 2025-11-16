package br.com.cxinvest.dto;

import br.com.cxinvest.entity.Produto;
import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidadeMensal,
        Produto.ClasseRisco classeRisco
) {
}

