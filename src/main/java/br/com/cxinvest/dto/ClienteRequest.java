package br.com.cxinvest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotNull Long perfilId
) {
}

