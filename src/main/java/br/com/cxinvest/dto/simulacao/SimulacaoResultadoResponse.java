package br.com.cxinvest.dto.simulacao;

import java.math.BigDecimal;

public record SimulacaoResultadoResponse(
        BigDecimal valorFinal,
        BigDecimal rentabilidadeEfetiva,
        Integer prazoMeses
) {
}
