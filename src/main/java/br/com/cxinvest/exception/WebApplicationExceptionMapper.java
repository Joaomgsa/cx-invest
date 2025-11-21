package br.com.cxinvest.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse() != null ? exception.getResponse().getStatus() : 500;
        String mensagem = exception.getMessage();
        Map<String, Object> body = Map.of(
                "httpCode", status,
                "mensagem", mensagem != null ? mensagem : "Erro de aplicação"
        );
        return Response.status(status)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

