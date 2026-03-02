package com.infinity.poc.infrastructure.adapter.out;

import com.infinity.poc.domain.model.OrderEvent;
import com.infinity.poc.domain.port.out.OrderEventPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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

    private final Counter kafkaPublishedCounter;
    private final Counter kafkaFailedCounter;
    private final Timer kafkaPublishTimer;

    public KafkaOrderEventAdapter(MeterRegistry registry) {
        this.kafkaPublishedCounter = Counter.builder("kafka.events.published.total")
                .description("Total de eventos publicados en Kafka")
                .register(registry);

        this.kafkaFailedCounter = Counter.builder("kafka.events.failed.total")
                .description("Total de eventos fallidos al publicar en Kafka")
                .register(registry);

        this.kafkaPublishTimer = Timer.builder("kafka.events.publish.duration")
                .description("Tiempo de publicacion de eventos en Kafka")
                .register(registry);
    }

    @Override
    public void publish(OrderEvent event) {
        kafkaPublishTimer.record(() -> {
            LOG.infof("Publicando evento orderId=%s al canal outgoing-order-events", event.orderId());
            try {
                emitter.send(event)
                        .await().atMost(Duration.ofSeconds(10));

                kafkaPublishedCounter.increment();
                LOG.infof("Evento publicado exitosamente orderId=%s", event.orderId());
            } catch (Exception e) {
                kafkaFailedCounter.increment();
                LOG.errorf(e, "Error publicando evento orderId=%s", event.orderId());
                throw e;
            }
        });
    }
}

