package br.com.cxinvest.dto.cliente;

import java.math.BigDecimal;

public record HistoricoInvestimentoResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        BigDecimal rentabilidade,
        String data
) {
}
