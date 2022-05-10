package com.redhat.examples.custommetricsdemo.endpoints;


import com.redhat.examples.custommetricsdemo.endpoints.EndpointResponse;
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
    public static final String totalNumberOfTimes = "number_of_times_reading_from_service";
    public static final String totalNumberOfFailures = "number_of_failure_attempts";
    public static final String totalNumberOfSuccesses = "number_of_successful_attempts";
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
        switch (name)
        {
            case totalNumberOfTimes:
                return  Integer.valueOf(testService.getNumberOfAccesses()).toString();
            case totalNumberOfSuccesses:
                return  Integer.valueOf(testService.getNumberOfSuccessfulAttempts()).toString();
            case totalNumberOfFailures:
                return  Integer.valueOf(testService.getNumberOfFailureAttempts()).toString();
            default:
                return "no such member property " + name + " for metric service-counter";

        }


    }


}
