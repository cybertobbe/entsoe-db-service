package tech.sjostrom.entsoedbservice;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PriceConsumerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("jms:queue:entsoe.prices")     //Will show up in ActiveMQ as "entsoe.prices" queue as an active consumer
                .routeId("price-consumer-route")
                .log("Received message from queue")
                .bean(PriceParserService.class, "parseAndSave")
                .log("Prices saved to database");
    }
}