package com.redhat.examples.custommetricsdemo.controllers;

import com.redhat.examples.custommetricsdemo.services.MicroMeterExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("place-order")

public class MicroMeterExampleController {

    private final MicroMeterExampleService microMeterExampleService;
    @GetMapping("/{itemName}")
    @ResponseStatus(HttpStatus.OK)
    public String placeOrder(@PathVariable String itemName, @RequestParam Integer amount)
    {
        microMeterExampleService.placeOrder(itemName,amount.doubleValue());
        return "Order of " + itemName + " with value of " + amount + " Completed successfully!!";
    }
}
