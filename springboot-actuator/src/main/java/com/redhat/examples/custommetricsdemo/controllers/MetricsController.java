package com.redhat.examples.custommetricsdemo.controllers;

import com.redhat.examples.custommetricsdemo.endpoints.EndpointResponse;
import com.redhat.examples.custommetricsdemo.endpoints.NumberOfTimesAccessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class MetricsController {

    @Autowired
    private NumberOfTimesAccessingService numberOfTimesAccessingService;
    @GetMapping("/metrics")
    public EndpointResponse getMetrics()
    {
        return numberOfTimesAccessingService.getMetric();
    }


}
