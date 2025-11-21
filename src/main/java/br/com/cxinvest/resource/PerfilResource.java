package br.com.cxinvest.resource;

import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.service.PerfilService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/perfis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilResource {

    @Inject
    PerfilService service;

    @GET
    @RolesAllowed({"admin", "analista", "cliente"})
    public List<Perfil> listar() {
        return service.listarTodos();
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"admin", "analista", "cliente"})
    public Response buscar(@PathParam("id") Long id) {
        return service.buscarPorId(id)
                .map(p -> Response.ok(p).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("admin")
    public Response criar(Perfil perfil) {
        Perfil criado = service.criar(perfil);
        return Response.created(URI.create("/perfis/" + criado.id)).entity(criado).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed("admin")
    public Response atualizar(@PathParam("id") Long id, Perfil perfil) {
        Perfil atualizado = service.atualizar(id, perfil);
        return Response.ok(atualizado).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }
}

