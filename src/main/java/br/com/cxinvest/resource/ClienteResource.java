package br.com.cxinvest.resource;

import br.com.cxinvest.dto.cliente.ClienteRequest;
import br.com.cxinvest.dto.cliente.ClienteResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.service.ClienteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteService service;

    @GET
    @RolesAllowed({"admin", "analista"})
    public List<ClienteResponse> listar() {
        return service.listarTodos().stream().map(service::toResponse).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    @RolesAllowed({"admin", "analista", "cliente"})
    public Response buscar(@PathParam("id") Long id) {
        Optional<Cliente> cliente = service.buscarPorId(id);
        return cliente.map(c -> Response.ok(service.toResponse(c)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("admin")
    public Response criar(ClienteRequest req) {
        Cliente cliente = service.toEntity(req);
        Cliente criado = service.criar(cliente);
        return Response.created(URI.create("/clientes/" + criado.id)).entity(service.toResponse(criado)).build();
    }

    @PUT
    @Path("{id}")
    @RolesAllowed({"admin", "cliente"})
    public Response atualizar(@PathParam("id") Long id, ClienteRequest req) {
        Cliente cliente = service.toEntity(req);
        Cliente atualizado = service.atualizar(id, cliente);
        return Response.ok(service.toResponse(atualizado)).build();
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed("admin")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }
}
