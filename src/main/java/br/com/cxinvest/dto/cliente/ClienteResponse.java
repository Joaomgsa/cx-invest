package br.com.cxinvest.dto.cliente;

import br.com.cxinvest.entity.Enum.FrequenciaInvestimento;
import br.com.cxinvest.entity.Enum.PreferenciaInvestimento;

import java.math.BigDecimal;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        Long perfilId,
        String perfilNome,
        BigDecimal totalInvestido,
        FrequenciaInvestimento frequenciaInvestimento,
        PreferenciaInvestimento preferenciaInvestimento
) {
}
