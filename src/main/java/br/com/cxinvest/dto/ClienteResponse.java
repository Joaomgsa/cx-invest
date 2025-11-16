package br.com.cxinvest.dto;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        Long perfilId,
        String perfilNome
) {
}

