package br.com.cxinvest.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ThrowableMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        Map<String, Object> body = Map.of(
                "httpCode", 500,
                "mensagem", exception.getMessage() != null ? exception.getMessage() : "Erro interno"
        );
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

