package br.com.cxinvest.dto.simulacao;

import br.com.cxinvest.entity.Produto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SimulacaoResponse(

        //Produto produtoValidado
        //resultado da simulação
) {
}
