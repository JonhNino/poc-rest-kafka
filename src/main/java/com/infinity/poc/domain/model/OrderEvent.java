package com.infinity.poc.domain.model;

import java.time.Instant;
import java.util.UUID;

public record OrderEvent(
        String orderId,
        String customerId,
        String productCode,
        Integer quantity,
        Double totalAmount,
        String deliveryAddress,
        String status,
        Instant createdAt
) {

    public static OrderEvent fromRequest(OrderRequest request) {
        return new OrderEvent(
                UUID.randomUUID().toString(),
                request.customerId(),
                request.productCode(),
                request.quantity(),
                request.quantity() * request.unitPrice(),
                request.deliveryAddress(),
                "CREATED",
                Instant.now()
        );
    }
}

