package br.com.cxinvest.dto.produto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public record ProdutoRecomendadoResponse(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        @JsonIgnore
        Long perfilId,
        @JsonIgnore
        String perfilNome,
        Risco risco
) {
}
