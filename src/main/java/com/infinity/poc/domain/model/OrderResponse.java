package com.infinity.poc.domain.model;

public record OrderResponse(
        String orderId,
        String status,
        String message
) {
}

