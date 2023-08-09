package id.swarawan.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoResilience4jApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoResilience4jApplication.class, args);
    }

}