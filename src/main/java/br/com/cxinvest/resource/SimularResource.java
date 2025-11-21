package br.com.cxinvest.resource;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * Recurso REST para endpoints de simulação (resumos por produto e histórico).
 */
@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
public class SimularResource {

    @Inject
    SimulacaoService simulacaoService;

    @POST
    @Path("/simular-investimento")
    public Response simular(@Valid SimulacaoRequest req) {
        return Response.ok(simulacaoService.simular(req)).build();
    }

}
