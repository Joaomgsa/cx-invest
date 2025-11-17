package br.com.cxinvest.validation;

import jakarta.validation.Payload;

public final class ValidationPayload {

    private ValidationPayload() {}

    public static final class Error implements Payload {}
    public static final class Warning implements Payload {}

}
