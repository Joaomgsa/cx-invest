package br.com.cxinvest.resource;

import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.service.ClienteService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @GET
    public List<Cliente> listar() {
        return service.listarTodos();
    }

    @GET
    @Path("{id}")
    public Response buscar(@PathParam("id") Long id) {
        Optional<Cliente> cliente = service.buscarPorId(id);
        return Response.ok(cliente).build();
    }

    @POST
    public Response criar(@Valid Cliente cliente) {
        Cliente criado = service.criar(cliente);
        return Response.created(URI.create("/clientes/" + criado.id)).entity(criado).build();
    }

    @PUT
    @Path("{id}")
    public Response atualizar(@PathParam("id") Long id, @Valid Cliente cliente) {
        Cliente atualizado = service.atualizar(id, cliente);
        return Response.ok(atualizado).build();
    }

    @DELETE
    @Path("{id}")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }
}

