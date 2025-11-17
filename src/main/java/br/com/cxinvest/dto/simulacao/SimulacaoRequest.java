package br.com.cxinvest.dto.simulacao;

import br.com.cxinvest.validation.ValidationPayload;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulacaoRequest (
        @NotNull(message = "O ID do cliente é obrigatório", payload = { ValidationPayload.Error.class })
        Long clienteId,
        @NotNull
        @DecimalMin(value = "1.00", message = "O valor mínimo para simulação é R$ 1,00", payload = { ValidationPayload.Error.class })
        BigDecimal valor,
        @NotNull
        @Min(value = 1 , message = "O prazo mínimo de investimento é de 1 mês", payload = { ValidationPayload.Error.class })
        Integer prazoMeses,
        @NotBlank(message = "O tipo do produto é obrigatório", payload = { ValidationPayload.Error.class })
        String tipoProduto
) {
}
