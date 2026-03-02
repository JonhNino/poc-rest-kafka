package com.infinity.poc.application.service;

import com.infinity.poc.domain.model.OrderEvent;
import com.infinity.poc.domain.model.OrderRequest;
import com.infinity.poc.domain.model.OrderResponse;
import com.infinity.poc.domain.port.in.ProcessOrderUseCase;
import com.infinity.poc.domain.port.out.OrderEventPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProcessOrderService implements ProcessOrderUseCase {

    private static final Logger LOG = Logger.getLogger(ProcessOrderService.class);

    private final OrderEventPort orderEventPort;
    private final Counter ordersProcessedCounter;
    private final Counter ordersFailedCounter;
    private final Timer orderProcessingTimer;

    public ProcessOrderService(OrderEventPort orderEventPort, MeterRegistry registry) {
        this.orderEventPort = orderEventPort;

        this.ordersProcessedCounter = Counter.builder("orders.processed.total")
                .description("Total de ordenes procesadas exitosamente")
                .register(registry);

        this.ordersFailedCounter = Counter.builder("orders.failed.total")
                .description("Total de ordenes fallidas")
                .register(registry);

        this.orderProcessingTimer = Timer.builder("orders.processing.duration")
                .description("Tiempo de procesamiento de ordenes")
                .register(registry);
    }

    @Override
    public OrderResponse execute(OrderRequest request) {
        return orderProcessingTimer.record(() -> {
            LOG.infof("Procesando orden para cliente=%s producto=%s", request.customerId(), request.productCode());

            try {
                OrderEvent event = OrderEvent.fromRequest(request);
                orderEventPort.publish(event);

                ordersProcessedCounter.increment();
                LOG.infof("Evento publicado orderId=%s totalAmount=%.2f", event.orderId(), event.totalAmount());

                return new OrderResponse(event.orderId(), event.status(), "Orden procesada y publicada en Kafka");
            } catch (Exception e) {
                ordersFailedCounter.increment();
                throw e;
            }
        });
    }
}

