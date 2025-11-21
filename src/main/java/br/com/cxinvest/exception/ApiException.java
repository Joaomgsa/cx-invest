package br.com.cxinvest.exception;

/**
 * Exceção de domínio para a API contendo um código HTTP e uma mensagem legível.
 * Pode ser lançada em serviços para sinalizar um erro com status HTTP específico.
 */
public class ApiException extends RuntimeException {
    private final int httpCode;
    private final String mensagem;

    public ApiException(int httpCode, String mensagem) {
        super(mensagem);
        this.httpCode = httpCode;
        this.mensagem = mensagem;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getMensagem() {
        return mensagem;
    }
}

