package br.com.cxinvest.resource;

import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.service.MetricsService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MetricResource {

    @Inject
    MetricsService service;

    @GET
    @RolesAllowed({"admin", "analista"})
    public Response getTelemetria(
            @QueryParam("inicio") String inicio,
            @QueryParam("fim") String fim,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {

        TelemetriaResponse resp = service.obterTelemetria(inicio, fim, page, size);
        return Response.ok(resp).build();
    }
}
