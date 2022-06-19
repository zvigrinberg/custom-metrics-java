package com.redhat.examples.custommetrics.micrometer.demo;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


@Data
@Slf4j
public class BookingService {

    private final int factorConstant = 987298141;
    private final int factorConstant2 = 827290631;
    private Counter numberOfBookings;
    private Counter amountOfMoney;
    private static BookingService bookingService=null;
    private Gauge successFailProportion;
    private List successFailProportionList;
    private Timer transactionTime;

    public static BookingService getInstance(PrometheusMeterRegistry prometheusMeterRegistry) {

        if(bookingService == null)
        {

            bookingService= new BookingService();

            bookingService.numberOfBookings = Counter.builder("number_of_bookings")
                                                     .register(prometheusMeterRegistry);

            bookingService.amountOfMoney = Counter.builder("amount_of_money")
                                                  .register(prometheusMeterRegistry);

            bookingService.transactionTime = Timer.builder("transaction.time")
                                             .description("times that takes to complete the transaction")
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
        Timer.Sample timer = Timer.start();
        numberOfBookings.increment();
        amountOfMoney.increment(money.doubleValue());
        log.info("start time of tran:" + LocalDateTime.now());
        //randomize process of deciding whether the booking process was stable or not, with a chance of 1/20 that transaction will not be stable.
        double randomNumber = Math.random() * factorConstant;
        int chance =  (int)randomNumber % 20;
        if (chance == 0)
        {
            successFailProportionList.add(false);
        }
        else
        {
            successFailProportionList.add(true);
        }
        double randomNumber2 = Math.random() * factorConstant2;
        int sleepTime = (int)randomNumber2 % 548;
        try {
            log.info("processing the transaction time randomized value : = " + sleepTime);
            sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        log.info("Got request to make booking of item " + item + " with value of " + money );
        log.info("end time of tran:" + LocalDateTime.now());
       timer.stop(transactionTime);
    }

}


