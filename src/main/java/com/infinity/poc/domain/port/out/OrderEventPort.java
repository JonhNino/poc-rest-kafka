package com.infinity.poc.domain.port.out;

import com.infinity.poc.domain.model.OrderEvent;

public interface OrderEventPort {

    void publish(OrderEvent event);
}

