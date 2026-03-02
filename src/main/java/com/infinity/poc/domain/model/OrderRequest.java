package com.infinity.poc.domain.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderRequest(

        @NotBlank(message = "customerId es obligatorio")
        String customerId,

        @NotBlank(message = "productCode es obligatorio")
        String productCode,

        @NotNull(message = "quantity es obligatorio")
        @Positive(message = "quantity debe ser mayor a 0")
        Integer quantity,

        @NotNull(message = "unitPrice es obligatorio")
        @DecimalMin(value = "0.01", message = "unitPrice debe ser mayor a 0")
        Double unitPrice,

        @NotBlank(message = "deliveryAddress es obligatorio")
        String deliveryAddress
) {
}

