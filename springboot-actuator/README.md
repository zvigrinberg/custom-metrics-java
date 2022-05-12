# How to Create Custom Metrics on a JAVA Spring Boot Application

## **Explanation:**

1. Create A Prometheus Metrics using Micrometer and expose it as endpoint with Spring boot actuator:
    ```java
    @Service
    @RequiredArgsConstructor
    public class MicroMeterExampleService {
    
        private final MeterRegistry meterRegistry;
        private Counter totalOrdersMade;
        private Counter totalRevenuesEarned;
    
        /** This method simulate placing an order, and increment 2 micrometer counters
         * @param orderId the order id/ order name
         * @param amount the amount of the order
         */
        public void placeOrder(String orderId, double amount)
        {
            System.out.println("Processing Order id= " +  orderId);
            totalOrdersMade.increment();
            totalRevenuesEarned.increment(amount);
    
        }
    
    
        /**
         * This method define and initialize two counters when the bean is created,
         * And registers it with the Meter Registry, these two counter metrics exposed
         * Automatically by Spring boot actuator on path /actuator/prometheus.
         */
        @PostConstruct
        public void initCounters()
        {
            this.totalOrdersMade = Counter.builder("orders_made")
                    .tag("type","all")
                    .description("The Total Number of orders that made")
                    .register(meterRegistry);
            this.totalRevenuesEarned = Counter.builder("revenues_earned")
                    .tag("type","all")
                    .description("The Total Number of money earned from orders")
                    .register(meterRegistry);
    
        }
    
    }
    
    ```
2. Related Application properties values:
   ```properties
   #Application port for applicative endpoints
   server.port=8081
   #let actuator expose prometheus metrics.
   management.endpoints.web.exposure.include=health,info,prometheus 
   # port for management server - all metrics endpoints are served via this port
   management.server.port=9090     
   ```
3. Notes:
   
   1. Injecting through Dependency Injection(In Particular - Constructor injection) a meter registry bean
      (Created automatically by Spring boot) in order to register Counter Metrics.
   2. `initCounters()` method creating the meters(metrics) and register them to meterRegistry,
      it's decorated with @PostConstruct annotation, that let spring invoke it once right after it creates this service bean.
   3. Metrics exposed automatically by spring boot actuator under the endpoint `/actuator/prometheus`(containing in addition all metrics that are being exposed by actuator, served on management port, in our configuration it's 9090).
   4. The service is invoked by [application controller(endpoint)](./src/main/java/com/redhat/examples/custommetricsdemo/controllers/MicroMeterExampleController.java) which trigger changing the metrics values(incrementing and appending amounts to them, accordingly).
   5. The Metrics that are defined here are: `orders_made_total` and `revenues_earned_total`(total is appended automatically by micrometer library as it's counters meters/metrics).
     

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
