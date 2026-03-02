package com.infinity.poc.domain.port.in;

import com.infinity.poc.domain.model.OrderRequest;
import com.infinity.poc.domain.model.OrderResponse;

public interface ProcessOrderUseCase {

    OrderResponse execute(OrderRequest request);
}

