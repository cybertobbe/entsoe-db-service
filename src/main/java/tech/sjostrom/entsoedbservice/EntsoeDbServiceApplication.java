package tech.sjostrom.entsoedbservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class EntsoeDbServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EntsoeDbServiceApplication.class, args);
    }

}
