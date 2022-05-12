# How to Create Custom Metrics on a JAVA Application Using MicroMeter

## **Explanation:**

1. This Code creates A Prometheus Metrics using Micrometer and expose it as endpoint Using JAVA HTTPServer object :
  ```java
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
    
   ```

**_Notes:_**
   - This is a singleton service class, which on initialization only, creates
     Metrics and register them with a prometheus meter registry.
   - when its method makeBooking is being called, It changes the values(increment)  
     of the metrics(counter metrics).
   - The Prometheus Meter Registry object is passed as an argument to the getInstance method,
     to let the created metrics the ability to register themselves with the registry.

2. This code is the main Application(for full code of the main program, [click here](./src/main/java/com/redhat/examples/custommetrics/micrometer/demo/JavaMicrometerApplication.java)) 
```java
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
                        /*hidden logic
                        ....
                         */
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
```
 Notes:
 - First the program creates a prometheus Meter registry.
 - Then it creates a single instance of BookingService(Singleton) by calling
   BookingService's static method getInstance and passing to it argument of the meter registry.
 - Then the application creates an HTTP Server that listens on port 8080, and creating two endpoints:\
     1. `/prometheus` endpoint, which returns all the registered metrics on the meter registry and their state(their values),
        it does so by calling the scrape() function of the PrometheusMeterRegistry class of micrometer library, which returns
        a String with the supported Prometheus text format with all metrics and their data, and this text 
        is sent as the reponse body back to the client that consumes this endpoint.
     2. `/make-booking` endpoint, which gets two url parameters, `item` and `amount`, parse them to
         variables, and if they are missing or invalid , then send a bad request response(Http code 400) 
        , but if everything is ok(item is not empty and amount is numeric) then it calls method makeBooking of the service,which in turn
          updates two counters metrics - first number_of_bookings - increment it by one, and then
          add the amount to counter amount_of_money, and returns status OK (HTTP Code 200). 
        
         
          

## Building And creating the image:
1. Build Jar file of the application:
```shell
[zgrinber@zgrinber springboot-actuator]$ ./mvnw clean package
```
2. Check that the jar with dependencies was built:
```shell
[zgrinber@zgrinber springboot-actuator]$ cd target
[zgrinber@zgrinber target]$ ll *.jar
-rw-rw-r--. 1 zgrinber zgrinber 73172414 May 11 01:40 custom-metrics-demo-0.0.1-SNAPSHOT.jar
[zgrinber@zgrinber target]$ 
```
3. Build the Image using docker or podman:
```shell
[zgrinber@zgrinber target]$ cd ..
[zgrinber@zgrinber springboot-actuator]$ podman build -t custom-metric:5 .
WARN[0000] missing "app" build argument. Try adding "--build-arg app=<VALUE>" to the command line 
STEP 1/10: FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:1.12-1.1651233103
STEP 2/10: USER root
--> Using cache 07f19bd2601aa99ff669e89dbdf4f1318f11947b0c6e8266b18145edcc2b02d3
--> 07f19bd2601
STEP 3/10: RUN useradd appuser     && mkdir /java-app     && chown appuser /java-app
--> Using cache 54a70f5436aed7a31e5e8c97b0b9fa03f8db41c7edcd8f6a34f24d5b362f8555
--> 54a70f5436a
STEP 4/10: ARG app
--> Using cache 0ad5b96ccbb91d5749c945f3753f07901738a1cf1b5bd5c764f683ddfd0a9371
--> 0ad5b96ccbb
STEP 5/10: COPY /target/*.jar /java-app/app.jar
--> Using cache 3ddf074fb9a4405a2c398cc63826a4efd4bff441e18a56407f2033381979061e
--> 3ddf074fb9a
STEP 6/10: RUN chmod -R ug+xrw /java-app
--> Using cache 4f89ae3b6487a3f86f6da9492368c3e3eb9b42233a9ac105425590bd0ac47619
--> 4f89ae3b648
STEP 7/10: USER appuser
--> Using cache bd4741527da81faab9c0aad7652b9883dea0e8fca0d2eddfc414224c4188e680
--> bd4741527da
STEP 8/10: EXPOSE 8081 9090
--> Using cache c20c7c6c62c196b570ea77b8794d150368c84dfbb47ef97e55823c95d369c1ba
--> c20c7c6c62c
STEP 9/10: WORKDIR /java-app
--> Using cache e9c1ddaeb112c989f05e34de5e9525e701e22e876a567de011bf7dd11f379e15
--> e9c1ddaeb11
STEP 10/10: ENTRYPOINT ["java", "-jar", "app.jar"]
--> Using cache dc224ce425df5cc6b6a2a59ee54f5be099db6642e642d11c4d1b6acb3f89ff69
COMMIT custom-metric:5
--> dc224ce425d
Successfully tagged localhost/custom-metric:4
dc224ce425df5cc6b6a2a59ee54f5be099db6642e642d11c4d1b6acb3f89ff69
```
4. Tag the local image name with a remote repo name and version, for example:
```shell
[zgrinber@zgrinber springboot-actuator]$ podman tag localhost/custom-metric:4 quay.io/zgrinber/custom-metric:4
```
5. Push the tagged image to container image registry:
```shell
[zgrinber@zgrinber springboot-actuator]$ podman push quay.io/zgrinber/custom-metric:4
```
