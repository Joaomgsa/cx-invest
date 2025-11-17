package br.com.cxinvest.dto.simulacao;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulacaoRequest (
        @NotNull
        Long clienteId,
        @NotNull
        @DecimalMin(value = "1.00")
        BigDecimal valor,
        @NotNull
        @Min(1)
        Integer prazoMeses,
        String tipoProduto
) {
}
