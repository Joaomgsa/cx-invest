package br.com.cxinvest.resource;

import br.com.cxinvest.dto.simulacao.SimulacaoProdutoDiaResponse;
import br.com.cxinvest.repository.SimulacaoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Recurso REST para endpoints de simulação (resumos por produto).
 */
@Path("/simulacos")
@Produces(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimulacaoRepository repository;

    /**
     * Retorna o resumo diário por produto com total de simulações e média do valor final.
     * Endpoint: GET /simulacos/por-produto-dia
     */
    @GET
    @Path("/por-produto-dia")
    public List<SimulacaoProdutoDiaResponse> porProdutoDia() {
        return repository.produtoDiaInfo();
    }
}

