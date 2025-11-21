package br.com.cxinvest.resource;

import br.com.cxinvest.dto.ProdutoResponse;
import br.com.cxinvest.service.ProdutoService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoPerfilResource {

    @Inject
    ProdutoService service;

    @GET
    @Path("{perfilId}")
    @RolesAllowed({"admin", "analista", "cliente"})
    public Response produtosRecomendados(@PathParam("perfilId") Long perfilId) {
        List<ProdutoResponse> produtos = service.produtosRecomendadosPerfil(perfilId);
        return Response.ok(produtos).build();
    }

}
