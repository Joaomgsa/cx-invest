package br.com.cxinvest.validation;

import jakarta.validation.Payload;

/**
 * Marcadores de payload usados em validações para categorizar mensagens.
 * - Error: representa uma falha de validação que impede a operação.
 * - Warning: representa uma advertência que pode ser apenas informativa.
 */
public final class ValidationPayload {

    private ValidationPayload() {}

    public static final class Error implements Payload {}
    public static final class Warning implements Payload {}

}
