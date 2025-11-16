package br.com.cxinvest.resource;

import br.com.cxinvest.dto.cliente.ClienteRequest;
import br.com.cxinvest.dto.cliente.ClienteResponse;
import br.com.cxinvest.entity.Cliente;
import br.com.cxinvest.entity.Perfil;
import br.com.cxinvest.service.ClienteService;
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
    public List<ClienteResponse> listar() {
        return service.listarTodos().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @GET
    @Path("{id}")
    public Response buscar(@PathParam("id") Long id) {
        Optional<Cliente> cliente = service.buscarPorId(id);
        return cliente.map(c -> Response.ok(toResponse(c)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(ClienteRequest req) {
        Cliente cliente = toEntity(req);
        Cliente criado = service.criar(cliente);
        return Response.created(URI.create("/clientes/" + criado.id)).entity(toResponse(criado)).build();
    }

    @PUT
    @Path("{id}")
    public Response atualizar(@PathParam("id") Long id, ClienteRequest req) {
        Cliente cliente = toEntity(req);
        Cliente atualizado = service.atualizar(id, cliente);
        return Response.ok(toResponse(atualizado)).build();
    }

    @DELETE
    @Path("{id}")
    public Response remover(@PathParam("id") Long id) {
        service.remover(id);
        return Response.noContent().build();
    }

    private ClienteResponse toResponse(Cliente c) {
        Long perfilId = c.perfilInvestimento != null ? c.perfilInvestimento.id : null;
        String perfilNome = c.perfilInvestimento != null ? c.perfilInvestimento.nome : null;
        return new ClienteResponse(c.id, c.nome, c.email, perfilId, perfilNome, c.totalInvestido, c.frequenciaInvestimento, c.preferenciaInvestimento);
    }

    private Cliente toEntity(ClienteRequest r) {
        Cliente c = new Cliente();
        c.nome = r.nome();
        c.email = r.email();
        c.totalInvestido = r.totalInvestido();
        c.frequenciaInvestimento = r.frequenciaInvestimento();
        c.preferenciaInvestimento = r.preferenciaInvestimento();
        Perfil perfil = new Perfil();
        perfil.id = r.perfilId();
        c.perfilInvestimento = perfil;
        return c;
    }
}
