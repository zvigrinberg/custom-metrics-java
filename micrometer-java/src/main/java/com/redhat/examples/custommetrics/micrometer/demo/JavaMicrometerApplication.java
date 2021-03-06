package com.redhat.examples.custommetrics.micrometer.demo;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
public class JavaMicrometerApplication {

    public static void main(String[] args) {
        log.info("Start of Demo Application - How to work with micrometer in plain-java and expose metrics to Prometheus");
        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        BookingService service = BookingService.getInstance(prometheusRegistry);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/prometheus", httpExchange -> {
                log.info("Got Request to pull metrics");
                String response = prometheusRegistry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            server.createContext("/make-booking" , httpExchange -> {
                        log.info("Handle request for /make-booking path");
                        String queryUrl = httpExchange.getRequestURI().getQuery();
                        Integer itemPos = queryUrl.indexOf("item=");
                        Integer amountPos = queryUrl.indexOf("amount=");
                        int itemEndPosition = getEndPosition(queryUrl, itemPos);
                        int amountEndPosition = getEndPosition(queryUrl, amountPos);
                        String[] item;
                        if(itemEndPosition == -1) {
                            item = queryUrl.substring(itemPos).split("=");
                        }
                        else
                        {
                            item = queryUrl.substring(itemPos, itemEndPosition).split("=");
                        }
                        String[] amount;
                        if(amountEndPosition == -1) {
                            amount = queryUrl.substring(amountPos).split("=");
                        }
                        else
                        {
                            amount = queryUrl.substring(itemPos, amountEndPosition).split("=");
                        }
                        String itemValue = item[1];
                        String amountValue = amount[1];
                        if(amountValue.isEmpty() || !StringUtils.isNumeric(amountValue) || itemValue.isEmpty()) {
                            String badRequestResponse = "Input parameters are missing or malformed";
                            log.error(badRequestResponse);
                            httpExchange.sendResponseHeaders(400, badRequestResponse.getBytes().length);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(badRequestResponse.getBytes());
                            }
                            Exception general_exception_test = new Exception("General exception test");
                            log.error("General error example",general_exception_test);
                        }
                        else
                        {
                                service.makeBooking(itemValue, Integer.parseInt(amountValue));
                                String okResponse = "booking performed successfully!!";
                                httpExchange.sendResponseHeaders(200, okResponse.getBytes().length);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(okResponse.getBytes());
                            }
                            log.info(okResponse + " With parameters : item " + itemValue + " and amount=" + amountValue);
                        }

                });

            new Thread(server::start).start();

//            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//                private Logger log = LoggerFactory.getLogger("JavaMicrometerApplication.class");
//                @Override
//                public void run() {
//                    log.info("Exiting Server, bye bye!");
//                }
//            }){
//
//            });

            Runtime.getRuntime().addShutdownHook(new Thread( ()-> {
                    Logger log = LoggerFactory.getLogger("JavaMicrometerApplication.class");
                    log.info("Exiting Server, bye bye!");
            }
            ));



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getEndPosition(String queryUrl, Integer itemPos) {
        return queryUrl.substring(itemPos).indexOf("&");
    }

}