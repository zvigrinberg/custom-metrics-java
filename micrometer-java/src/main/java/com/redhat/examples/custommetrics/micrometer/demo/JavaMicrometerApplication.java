package com.redhat.examples.custommetrics.micrometer.demo;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class JavaMicrometerApplication {

    public static void main(String[] args) {

        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        BookingService service = BookingService.getInstance(prometheusRegistry);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/prometheus", httpExchange -> {
                String response = prometheusRegistry.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            server.createContext("/make-booking" , httpExchange -> {
                        System.out.println("inside make booking");
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
                            httpExchange.sendResponseHeaders(400, badRequestResponse.getBytes().length);
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(badRequestResponse.getBytes());
                            }
                        }
                        else
                        {
                                service.makeBooking(itemValue, Integer.parseInt(amountValue));
                                String okResponse = "booking performed successfully!!";
                                httpExchange.sendResponseHeaders(200, okResponse.getBytes().length);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(okResponse.getBytes());
                            }
                        }

                });

            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getEndPosition(String queryUrl, Integer itemPos) {
        return queryUrl.substring(itemPos).indexOf("&");
    }

}