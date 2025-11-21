package br.com.cxinvest.dto.produto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProdutoRequest(
        @NotBlank String nome,
        @NotBlank String tipo,
        @NotNull BigDecimal rentabilidadeMensal,
        @NotNull Long perfilId
) {
}
