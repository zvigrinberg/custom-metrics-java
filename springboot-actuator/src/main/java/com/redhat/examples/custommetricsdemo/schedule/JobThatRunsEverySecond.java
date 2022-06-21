package com.redhat.examples.custommetricsdemo.schedule;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class JobThatRunsEverySecond {

  private final MeterRegistry meterRegistry;
  private Counter totalSecondsElapsed;


  @Scheduled(fixedDelay = 1000)
  public void getDifferent()
  {
    totalSecondsElapsed.increment();
  }

  @PostConstruct
  public void initialCounter()
  {
    this.totalSecondsElapsed = Counter.builder("demo_total_seconds")
            .description("The Total number of seconds elapsed since service started up")
            .register(meterRegistry);
  }

}

