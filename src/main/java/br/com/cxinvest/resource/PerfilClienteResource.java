package br.com.cxinvest.resource;


import br.com.cxinvest.service.ClienteService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;



@Path("/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerfilClienteResource {

    @Inject
    ClienteService service;

    @GET
    @Path("{clienteId}")
    @RolesAllowed({"admin", "analista", "cliente"})
    public Response perfilRiscoCliente(@PathParam("clienteId")  Long clienteId) {
        var perfil = service.perfilRiscoCliente(clienteId);
        return Response.ok(perfil).build();
    }


}
