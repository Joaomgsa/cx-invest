package br.com.cxinvest.config;

import io.quarkus.arc.profile.IfBuildProfile;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.security.Principal;

@Provider
@IfBuildProfile("dev")
@Priority(Priorities.AUTHENTICATION)
public class DevSecurityFilter implements ContainerRequestFilter {

    private static final String DEV_HEADER = "X-DEV-ADMIN";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String header = requestContext.getHeaderString(DEV_HEADER);
        if (header != null && ("true".equalsIgnoreCase(header) || "1".equals(header))) {
            SecurityContext devCtx = new SecurityContext() {
                private final Principal principal = () -> "dev-admin";

                @Override
                public Principal getUserPrincipal() {
                    return principal;
                }

                @Override
                public boolean isUserInRole(String role) {
                    // Quarkus usually checks role names directly (e.g. "admin").
                    if (role == null) return false;
                    return "admin".equals(role) || "ROLE_ADMIN".equals(role);
                }

                @Override
                public boolean isSecure() {
                    return requestContext.getUriInfo().getRequestUri().getScheme().equalsIgnoreCase("https");
                }

                @Override
                public String getAuthenticationScheme() {
                    return "DEV-HEADER";
                }
            };
            requestContext.setSecurityContext(devCtx);
        }
    }
}

