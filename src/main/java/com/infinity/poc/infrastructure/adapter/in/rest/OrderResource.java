package com.infinity.poc.infrastructure.adapter.in.rest;

import com.infinity.poc.domain.model.OrderRequest;
import com.infinity.poc.domain.model.OrderResponse;
import com.infinity.poc.domain.port.in.ProcessOrderUseCase;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderResource {

    private static final Logger LOG = Logger.getLogger(OrderResource.class);

    private final ProcessOrderUseCase processOrderUseCase;

    public OrderResource(ProcessOrderUseCase processOrderUseCase) {
        this.processOrderUseCase = processOrderUseCase;
    }

    @POST
    public Response createOrder(@Valid OrderRequest request) {
        LOG.infof("POST /orders recibido customerId=%s", request.customerId());

        OrderResponse response = processOrderUseCase.execute(request);

        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}

