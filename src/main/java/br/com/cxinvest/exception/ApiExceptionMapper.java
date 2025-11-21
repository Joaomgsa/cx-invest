package br.com.cxinvest.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Override
    public Response toResponse(ApiException exception) {
        Map<String, Object> body = Map.of(
                "httpCode", exception.getHttpCode(),
                "mensagem", exception.getMensagem()
        );
        return Response.status(exception.getHttpCode())
                .entity(body)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

