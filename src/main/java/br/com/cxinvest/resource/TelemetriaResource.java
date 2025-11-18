package br.com.cxinvest.resource;

import br.com.cxinvest.dto.telemetria.TelemetriaResponse;
import br.com.cxinvest.service.TelemetriaService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    TelemetriaService telemetriaService;

    @GET
    public Response getTelemetria(@QueryParam("inicio") String inicio,
                                  @QueryParam("fim") String fim,
                                  @QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("size") @DefaultValue("10") int size) {
        TelemetriaResponse resp = telemetriaService.obterTelemetria(inicio, fim, page, size);
        return Response.ok(resp).build();
    }
}
