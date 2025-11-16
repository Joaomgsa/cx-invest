package br.com.cxinvest.resource;

import br.com.cxinvest.dto.*;
import br.com.cxinvest.service.*;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InvestimentoResource {

    @Inject
    SimulacaoService simulacaoService;

    @Inject
    PerfilRiscoService perfilRiscoService;

    @Inject
    ProdutoService produtoService;

    @Inject
    InvestimentoService investimentoService;

    @Inject
    TelemetriaService telemetriaService;

    @POST
    @Path("/simular-investimento")
    public Response simularInvestimento(SimulacaoRequest request) {
        try {
            SimulacaoResponse response = simulacaoService.simularInvestimento(request);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao processar simulação: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/simulacoes")
    public Response listarSimulacoes() {
        try {
            List<SimulacaoHistoricoResponse> simulacoes = simulacaoService.listarSimulacoes();
            return Response.ok(simulacoes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao listar simulações: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/simulacoes/por-produto-dia")
    public Response listarSimulacoesPorProdutoDia() {
        try {
            List<SimulacaoPorProdutoDiaResponse> simulacoes = simulacaoService.listarSimulacoesPorProdutoDia();
            return Response.ok(simulacoes).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao listar simulações por produto/dia: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/perfil-risco/{clienteId}")
    public Response obterPerfilRisco(@PathParam("clienteId") Long clienteId) {
        try {
            PerfilRiscoResponse response = perfilRiscoService.calcularPerfilRisco(clienteId);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao calcular perfil de risco: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/produtos-recomendados/{perfil}")
    public Response listarProdutosRecomendados(@PathParam("perfil") String perfil) {
        try {
            List<ProdutoResponse> produtos = produtoService.listarProdutosPorPerfil(perfil);
            return Response.ok(produtos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao listar produtos recomendados: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/investimentos/{clienteId}")
    public Response listarInvestimentos(@PathParam("clienteId") Long clienteId) {
        try {
            List<InvestimentoResponse> investimentos = investimentoService.listarInvestimentos(clienteId);
            return Response.ok(investimentos).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao listar investimentos: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/telemetria")
    public Response obterTelemetria() {
        try {
            TelemetriaResponse response = telemetriaService.obterEstatisticas();
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Erro ao obter telemetria: " + e.getMessage()))
                    .build();
        }
    }

    public static class ErrorResponse {
        private String message;

        public ErrorResponse() {
        }

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
