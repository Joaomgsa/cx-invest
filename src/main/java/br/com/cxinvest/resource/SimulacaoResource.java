package br.com.cxinvest.resource;

import br.com.cxinvest.dto.simulacao.SimulacaoHistoricoResponse;
import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.service.SimulacaoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.core.MediaType;

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
    public List<SimulacaoHistoricoResponse> historico(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return simulacaoService.historico(page, size);
    }
}
