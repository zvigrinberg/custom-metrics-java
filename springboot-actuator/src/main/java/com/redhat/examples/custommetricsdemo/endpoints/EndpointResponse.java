package com.redhat.examples.custommetricsdemo.endpoints;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.ALWAYS)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointResponse {

    private Integer numberOfTimesReadingFromService;
    private Integer numberOfSuccessfulAttempts;
    private Integer numberOfFailureAttempts;
}
