package com.redhat.examples.custommetricsdemo.endpoints;


import com.redhat.examples.custommetricsdemo.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Endpoint(id = "service-counter")
public class NumberOfTimesAccessingService{
    private final TestService testService;
    @ReadOperation
    public EndpointResponse getMetric() {
        int numberOfAccesses = testService.getNumberOfAccesses();
        int numberOfFailureAttempts = testService.getNumberOfFailureAttempts();
        int numberOfSuccessfulAttempts = testService.getNumberOfSuccessfulAttempts();
        EndpointResponse response = new EndpointResponse(numberOfAccesses, numberOfSuccessfulAttempts,numberOfFailureAttempts);
        return response;

    }

    @ReadOperation
    public String customEndPointByName(@Selector String name) {
        return "custom-end-point";
    }


}
