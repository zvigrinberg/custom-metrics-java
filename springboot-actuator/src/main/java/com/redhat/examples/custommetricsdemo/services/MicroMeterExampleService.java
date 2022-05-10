package com.redhat.examples.custommetricsdemo.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class MicroMeterExampleService {

    private final MeterRegistry meterRegistry;
    private Counter totalOrdersMade;
    private Counter totalRevenuesEarned;

    /** This method simulate placing an order, and increment 2 micrometer counters
     * @param orderId the order id/ order name
     * @param amount the amount of the order
     */
    public void placeOrder(String orderId, double amount)
    {
        System.out.println("Processing Order id= " +  orderId);
        totalOrdersMade.increment();
        totalRevenuesEarned.increment(amount);

    }


    /**
     * This method define and initialize two counters when the bean is created,
     * And registers it with the Meter Registry, these two counter metrics exposed
     * Automatically by Spring boot actuator on path /actuator/prometheus.
     */
    @PostConstruct
    public void initCounters()
    {
        this.totalOrdersMade = Counter.builder("orders_made")
                .tag("type","all")
                .description("The Total Number of orders that made")
                .register(meterRegistry);
        this.totalRevenuesEarned = Counter.builder("revenues_earned")
                .tag("type","all")
                .description("The Total Number of money earned from orders")
                .register(meterRegistry);

    }

}
