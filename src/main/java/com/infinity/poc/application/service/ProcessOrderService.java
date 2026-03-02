package com.infinity.poc.application.service;

import com.infinity.poc.domain.model.OrderEvent;
import com.infinity.poc.domain.model.OrderRequest;
import com.infinity.poc.domain.model.OrderResponse;
import com.infinity.poc.domain.port.in.ProcessOrderUseCase;
import com.infinity.poc.domain.port.out.OrderEventPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProcessOrderService implements ProcessOrderUseCase {

    private static final Logger LOG = Logger.getLogger(ProcessOrderService.class);

    private final OrderEventPort orderEventPort;

    public ProcessOrderService(OrderEventPort orderEventPort) {
        this.orderEventPort = orderEventPort;
    }

    @Override
    public OrderResponse execute(OrderRequest request) {
        LOG.infof("Procesando orden para cliente=%s producto=%s", request.customerId(), request.productCode());

        OrderEvent event = OrderEvent.fromRequest(request);

        orderEventPort.publish(event);

        LOG.infof("Evento publicado orderId=%s totalAmount=%.2f", event.orderId(), event.totalAmount());

        return new OrderResponse(event.orderId(), event.status(), "Orden procesada y publicada en Kafka");
    }
}

