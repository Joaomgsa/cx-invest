package br.com.cxinvest.resource;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoRequest;
import br.com.cxinvest.service.SimulacaoService;
import jakarta.annotation.security.RolesAllowed;
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
public class SimulacaoResource {

    @Inject
    SimulacaoService simulacaoService;

    /**
     * Retorna o resumo diário por produto com total de simulações e média do valor final.
     * Endpoint: GET /simulacoes/por-produto-dia?page=0&size=10
     */
    @GET
    @Path("/por-produto-dia")
    @RolesAllowed({"admin", "analista"})
    public List<SimulacaoProdutoDiaResponse> porProdutoDia(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return simulacaoService.produtoDia(page, size);
    }

    /**
     * Retorna histórico paginado de simulações de todos os clientes.
     * Endpoint: GET /simulacoes?page=0&size=10
     */
    @GET
    @Path("/")
    @RolesAllowed({"admin", "analista"})
    public List<SimulacaoHistoricoResponse> historico(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return simulacaoService.historico(page, size);
    }

}
