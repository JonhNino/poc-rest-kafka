package com.infinity.poc.infrastructure.adapter.out;

import com.infinity.poc.domain.model.OrderEvent;
import com.infinity.poc.domain.port.out.OrderEventPort;
import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.jboss.logging.Logger;

import java.time.Duration;

@ApplicationScoped
public class KafkaOrderEventAdapter implements OrderEventPort {

    private static final Logger LOG = Logger.getLogger(KafkaOrderEventAdapter.class);

    @Inject
    @Channel("outgoing-order-events")
    MutinyEmitter<OrderEvent> emitter;

    @Override
    public void publish(OrderEvent event) {
        LOG.infof("Publicando evento orderId=%s al canal outgoing-order-events", event.orderId());

        emitter.send(event)
                .await().atMost(Duration.ofSeconds(10));

        LOG.infof("Evento publicado exitosamente orderId=%s", event.orderId());
    }
}

