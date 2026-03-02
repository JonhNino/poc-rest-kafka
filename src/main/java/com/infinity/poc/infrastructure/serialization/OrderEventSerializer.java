package com.infinity.poc.infrastructure.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.infinity.poc.domain.model.OrderEvent;
import org.apache.kafka.common.serialization.Serializer;
import org.jboss.logging.Logger;

import java.nio.charset.StandardCharsets;

public class OrderEventSerializer implements Serializer<OrderEvent> {

    private static final Logger LOG = Logger.getLogger(OrderEventSerializer.class);
    private final ObjectMapper objectMapper;

    public OrderEventSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public byte[] serialize(String topic, OrderEvent data) {
        if (data == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            LOG.errorf(e, "Error serializando OrderEvent orderId=%s", data.orderId());
            throw new RuntimeException("Error al serializar OrderEvent", e);
        }
    }
}

