package br.com.cxinvest.dto.cliente;

import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        Long perfilId,
        @NotNull BigDecimal totalInvestido,
        @NotNull FrequenciaInvestimento frequenciaInvestimento,
        @NotNull PreferenciaInvestimento preferenciaInvestimento
) {
}
