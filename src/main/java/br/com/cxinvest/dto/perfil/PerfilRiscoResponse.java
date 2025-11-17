package br.com.cxinvest.dto.perfil;

public record PerfilRiscoResponse(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) {
}
