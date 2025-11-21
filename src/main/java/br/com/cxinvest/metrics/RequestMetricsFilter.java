package br.com.cxinvest.metrics;

import br.com.cxinvest.entity.RequestMetric;
import br.com.cxinvest.service.MetricsService;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.regex.Pattern;

@Provider
@Priority(Priorities.USER)
public class RequestMetricsFilter implements ContainerRequestFilter, ContainerResponseFilter {


    private static final String START_TIME_PROPERTY = "RequestMetricsFilter.startTime";

    @Inject
    MetricsService metricsService;

    // Prefixos de paths a serem ignorados nas métricas (sem leading '/').
    private static final String[] IGNORE_PATH_PREFIXES = {
            "q/",
            "health",
            "metrics"
    };

    // Patterns para detectar segmentos que devem ser parametrizados
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("^\\d+$");

    @Override
    public void filter(ContainerRequestContext requestContext) {
        // marca o tempo de início
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        Object startObj = requestContext.getProperty(START_TIME_PROPERTY);
        if (startObj instanceof Long) {
            long start = (Long) startObj;
            long duration = System.currentTimeMillis() - start;

            String rawPath = requestContext.getUriInfo().getPath(); // caminho requisitado (pode ajustar para usar template)
            String pathNoLeading = (rawPath == null) ? "" : (rawPath.startsWith("/") ? rawPath.substring(1) : rawPath);

            // Ignora caminhos configurados (ex: /q/*, /health, /metrics)
            for (String prefix : IGNORE_PATH_PREFIXES) {
                if (pathNoLeading.startsWith(prefix)) {
                    return; // não registra métrica
                }
            }

            String normalizedPath = normalizePath(rawPath);

            String method = requestContext.getMethod();
            int status = responseContext.getStatus();

            RequestMetric metric = new RequestMetric(
                    normalizedPath,
                    method,
                    status,
                    duration,
                    Instant.now()
            );

            // Enfileira para persistência assíncrona (rápido)
            metricsService.enqueue(metric);
        }
    }

    /**
     * Normaliza o caminho retornando apenas o primeiro segmento.
     * Exemplos:
     *  "/clientes" -> "/clientes"
     *  "/clientes/123" -> "/clientes"
     *  "/clientes/550e8.../detalhe" -> "/clientes"
     */
    private static String normalizePath(String path) {
        if (path == null || path.isEmpty()) {
            return "/";
        }

        String pathNoLeading = path.startsWith("/") ? path.substring(1) : path;
        String[] parts = pathNoLeading.split("/");
        for (String part : parts) {
            if (part == null || part.isEmpty()) {
                continue;
            }
            String normalizedSegment = part;
            if (DIGITS_PATTERN.matcher(part).matches() || UUID_PATTERN.matcher(part).matches()) {
                normalizedSegment = "{param}"; // se o primeiro segmento for um id, normaliza como {param}
            }
            return "/" + normalizedSegment; // apenas o primeiro segmento
        }
        return "/";
    }


}
