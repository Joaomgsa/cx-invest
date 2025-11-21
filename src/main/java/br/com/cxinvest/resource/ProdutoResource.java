package br.com.cxinvest.resource;

import br.com.cxinvest.dto.produto.ProdutoRequest;
import br.com.cxinvest.dto.produto.ProdutoResponse;
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

@Path("/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {

    @Inject
    ProdutoService service;

    @GET
    public List<ProdutoResponse> listar() {
        return service.listarTodos().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    public Response buscar(@PathParam("id") Long id) {
        return service.buscarPorId(id).map(p -> Response.ok(toResponse(p)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(ProdutoRequest req) {
        Produto produto = toEntity(req);
        Produto criado = service.criar(produto);
        return Response.created(URI.create("/produtos/" + criado.id)).entity(toResponse(criado)).build();
    }

    @PUT
    @Path("{id}")
    public Response atualizar(@PathParam("id") Long id, ProdutoRequest req) {
        Produto produto = toEntity(req);
        Produto atualizado = service.atualizar(id, produto);
        return Response.ok(toResponse(atualizado)).build();
    }

    @DELETE
    @Path("{id}")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }

    private ProdutoResponse toResponse(Produto p) {
        Long perfilId = p.perfilInvestimento != null ? p.perfilInvestimento.id : null;
        String perfilNome = p.perfilInvestimento != null ? p.perfilInvestimento.nome : null;
        return new ProdutoResponse(p.id, p.nome, p.tipo, p.rentabilidadeMensal, perfilId, perfilNome);
    }

    private Produto toEntity(ProdutoRequest r) {
        Produto p = new Produto();
        p.nome = r.nome();
        p.tipo = r.tipo();
        p.rentabilidadeMensal = r.rentabilidadeMensal();
        Perfil perfil = new Perfil();
        perfil.id = r.perfilId();
        p.perfilInvestimento = perfil;
        return p;
    }
}
