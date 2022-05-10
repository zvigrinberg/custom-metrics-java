package com.redhat.examples.custommetricsdemo.controllers;

import com.redhat.examples.custommetricsdemo.endpoints.EndpointResponse;
import com.redhat.examples.custommetricsdemo.endpoints.NumberOfTimesAccessingService;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;




@RestController
@RequestMapping
@Timed
public class ManualMetricsController {

    @Autowired
    private NumberOfTimesAccessingService numberOfTimesAccessingService;

    /**
     * @return metrics text-based format Of Prometheus
     */
    @GetMapping(value = "/metrics",produces = "text/plain")

    public String getMetrics()
    {

        EndpointResponse metric = numberOfTimesAccessingService.getMetric();
        StringBuilder sb = new StringBuilder();
        sb.append("# HELP " + NumberOfTimesAccessingService.totalNumberOfTimes + " total number of attempts accessing the service" + System.lineSeparator());
        sb.append("# TYPE " + NumberOfTimesAccessingService.totalNumberOfTimes + " counter" + System.lineSeparator());
        sb.append(NumberOfTimesAccessingService.totalNumberOfTimes + " " + metric.getNumberOfTimesReadingFromService() + System.lineSeparator());
        sb.append("# HELP " + NumberOfTimesAccessingService.totalNumberOfSuccesses + " total number of successful attempts of accessing the service" + System.lineSeparator());
        sb.append("# TYPE " + NumberOfTimesAccessingService.totalNumberOfSuccesses + " counter" + System.lineSeparator());
        sb.append(NumberOfTimesAccessingService.totalNumberOfSuccesses + " " + metric.getNumberOfSuccessfulAttempts() + System.lineSeparator());
        sb.append("# HELP " + NumberOfTimesAccessingService.totalNumberOfFailures + " total number of failure attempts of accessing the service" + System.lineSeparator());
        sb.append("# TYPE " + NumberOfTimesAccessingService.totalNumberOfFailures + " counter" + System.lineSeparator());
        sb.append(NumberOfTimesAccessingService.totalNumberOfFailures + " " + metric.getNumberOfFailureAttempts() + System.lineSeparator());
        return sb.toString();
    }


}
