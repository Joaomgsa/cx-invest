package br.com.cxinvest.resource;

import br.com.cxinvest.dto.ProdutoRequest;
import br.com.cxinvest.dto.ProdutoResponse;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.entity.Produto;
import br.com.cxinvest.service.ProdutoService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoPerfilResource {

    @Inject
    ProdutoService service;

    @GET
    @Path("{perfil}")
    public Response produtosRecomendados(@PathParam("perfil") String perfil) {
        var produtos = service.
        return null;
    }


    @Path("{clienteId}")
    public Response perfilRiscoCliente(@PathParam("clienteId")  Long clienteId) {
        var perfil = service.perfilRiscoCliente(clienteId);
        return Response.ok(perfil).build();
    }

}
