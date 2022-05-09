package com.redhat.examples.custommetricsdemo.endpoints;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.ALWAYS)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointResponse {
    @JsonProperty(value = NumberOfTimesAccessingService.totalNumberOfTimes)
    private Integer numberOfTimesReadingFromService;
    @JsonProperty(value = NumberOfTimesAccessingService.totalNumberOfSuccesses )
    private Integer numberOfSuccessfulAttempts;
    @JsonProperty(value = NumberOfTimesAccessingService.totalNumberOfFailures)
    private Integer numberOfFailureAttempts;
}
