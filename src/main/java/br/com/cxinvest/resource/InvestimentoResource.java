package br.com.cxinvest.resource;

import br.com.cxinvest.dto.cliente.HistoricoInvestimentoResponse;
import br.com.cxinvest.service.InvestimentoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
public class InvestimentoResource {

    @Inject
    InvestimentoService investimentoService;

    @GET
    @Path("{clienteId}")
    public Response listarHistorico(@PathParam("clienteId") Long clienteId,
                                   @QueryParam("page") @DefaultValue("0") int page,
                                   @QueryParam("size") @DefaultValue("10") int size,
                                   @QueryParam("order") @DefaultValue("DESC") String order) {
        boolean asc = "ASC".equalsIgnoreCase(order);
        List<HistoricoInvestimentoResponse> lista = investimentoService.listarHistoricoInvestimentosCliente(clienteId, page, size, asc);
        return Response.ok(lista).build();
    }
}

