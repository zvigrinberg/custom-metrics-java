package com.redhat.examples.custommetrics.micrometer.demo;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;


@Data
@Slf4j
public class BookingService {

    private Counter numberOfBookings;
    private Counter amountOfMoney;
    private static BookingService bookingService=null;
    private Gauge successFailProportion;
    private List successFailProportionList;

    public static BookingService getInstance(PrometheusMeterRegistry prometheusMeterRegistry) {

        if(bookingService == null)
        {

            bookingService= new BookingService();

            bookingService.numberOfBookings = Counter.builder("number_of_bookings")
                                                     .register(prometheusMeterRegistry);

            bookingService.amountOfMoney = Counter.builder("amount_of_money")
                                                  .register(prometheusMeterRegistry);
            bookingService.successFailProportionList = new ArrayList(){};
            bookingService.successFailProportionList.add(true);
            bookingService.successFailProportionList.add(false);
            bookingService.successFailProportion = Gauge
                                                  .builder("stable_unstable_proportion",bookingService.successFailProportionList , value -> (double)(value.stream().filter(o-> o.equals(false)).count()) / (value.stream().filter(o -> o.equals(true)).count()))
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
        //randomize process of deciding whether the booking process was stable or not, with a chance of 1/5 that transaction will not be stable.
        double randomNumber = Math.random() * 987298141;
        int chance =  (int)randomNumber % 20;
        if (chance == 0)
        {
            successFailProportionList.add(false);
        }
        else
        {
            successFailProportionList.add(true);
        }

       log.info("Got request to make booking of item " + item + " with value of " + money );

    }

}


