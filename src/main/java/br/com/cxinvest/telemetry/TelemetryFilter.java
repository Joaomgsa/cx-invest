package br.com.cxinvest.telemetry;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.trace.Span;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Provider
public class TelemetryFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String START_TIME = "telemetry.startTime";

    // Prefixos de paths a serem ignorados nas métricas
    private static final String[] IGNORE_PATH_PREFIXES = {
            "q/",        // endpoints internos do Quarkus (/q/metrics, /q/health etc.)
            "health",    // caso exista mapeado sem /q
            "metrics"    // caso exista mapeado sem /q
    };

    @Inject
    MeterRegistry meterRegistry;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object o = requestContext.getProperty(START_TIME);
        if (!(o instanceof Long startNs)) {
            return;
        }

        long durationNs = System.nanoTime() - startNs;

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        String status = Integer.toString(responseContext.getStatus());

        // Normalizar path e ignorar alguns endpoints (como /q/*, /health, /metrics)
        String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
        for (String prefix : IGNORE_PATH_PREFIXES) {
            if (normalizedPath.startsWith(prefix)) {
                return; // não registra métrica nem atributos de span
            }
        }

        Timer.builder("http_server_requests_seconds")
                .description("HTTP server requests")
                .tag("method", method)
                .tag("uri", path)
                .tag("status", status)
                .register(meterRegistry)
                .record(durationNs, TimeUnit.NANOSECONDS);

        Span span = Span.current();
        span.setAttribute("http.duration_ms", TimeUnit.NANOSECONDS.toMillis(durationNs));
        span.setAttribute("http.custom.uri", path);
        span.setAttribute("http.custom.method", method);
        span.setAttribute("http.custom.status", status);
    }
}
