package com.infinity.poc.infrastructure.adapter.in.provider;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;

@Provider
public class RequestInterceptorFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(RequestInterceptorFilter.class);

    @ConfigProperty(name = "app.security.api-token")
    String expectedToken;

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

        // Solo interceptar endpoints de orders
        if (!path.startsWith("orders")) {
            return;
        }

        LOG.debugf("Interceptando request method=%s path=%s", ctx.getMethod(), path);

        // 1. Validar header Authorization presente y con formato Bearer
        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            LOG.warn("Request sin header Authorization");
            abort(ctx, Response.Status.UNAUTHORIZED, "Header Authorization es requerido");
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            LOG.warn("Formato de Authorization inválido, se espera Bearer <token>");
            abort(ctx, Response.Status.UNAUTHORIZED, "Formato inválido. Se espera: Bearer <token>");
            return;
        }

        String token = authHeader.substring(7).trim();
        if (!expectedToken.equals(token)) {
            LOG.warnf("Token inválido recibido: %s", token);
            abort(ctx, Response.Status.FORBIDDEN, "Token no autorizado");
            return;
        }

        // 2. Validar Content-Type sea application/json
        if (ctx.getMediaType() == null || !ctx.getMediaType().isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            LOG.warn("Content-Type no es application/json");
            abort(ctx, Response.Status.UNSUPPORTED_MEDIA_TYPE, "Content-Type debe ser application/json");
            return;
        }

        LOG.debug("Request validado correctamente por el interceptor");
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String message) {
        ctx.abortWith(Response.status(status)
                .entity("{\"error\":\"" + message + "\"}")
                .type(MediaType.APPLICATION_JSON)
                .build());
    }
}

