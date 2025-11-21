package br.com.cxinvest.resource;

import br.com.cxinvest.dto.produto.ProdutoRecomendadoResponse;
import br.com.cxinvest.service.ProdutoService;
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
    @Path("{perfil}")
    public Response produtosRecomendadosPorPerfil(@PathParam("perfil") String perfil) {
        List<ProdutoRecomendadoResponse> produtos = service.produtosRecomendadosPerfilNome(perfil);
        return Response.ok(produtos).build();
    }
}
