package com.redhat.examples.custommetricsdemo.controllers;

import com.redhat.examples.custommetricsdemo.services.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TestController {

    private final TestService testService;

    @GetMapping("/test-data/{number}")
    public String getDataFromService(@PathVariable Integer number)
    {
           return testService.getData(number);
    }

    @GetMapping("hello")
    public String getData()
    {
        return "Hello from Controller!";
    }
}
