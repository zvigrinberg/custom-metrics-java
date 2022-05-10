package com.redhat.examples.custommetrics.micrometer.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
public class BookingService {

    private Counter numberOfBookings;
    private Counter amountOfMoney;
    private static BookingService bookingService=null;

    public static BookingService getInstance(PrometheusMeterRegistry prometheusMeterRegistry) {

        if(bookingService == null)
        {
            bookingService= new BookingService();

            bookingService.numberOfBookings = Counter.builder("number_of_bookings")
                                                     .register(prometheusMeterRegistry);

            bookingService.amountOfMoney = Counter.builder("amount_of_money")
                                                  .register(prometheusMeterRegistry);

        }


        return bookingService;
    }

    private BookingService()
    {

    }

    public void makeBooking(String item,Integer money)
    {
        numberOfBookings.increment();
        amountOfMoney.increment(money.doubleValue());
        log.info("Got request to make booking of item " + item + " with value of " + money );
    }

}


