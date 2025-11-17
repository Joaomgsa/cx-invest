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

@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilClienteResource {

    @Inject
    ClienteService service;

    @Path("{clienteId}")
    public Response perfilRiscoCliente(@PathParam("clienteId")  Long clienteId) {
        var perfil = service.perfilRiscoCliente(clienteId);
        return Response.ok(perfil).build();
    }


}
