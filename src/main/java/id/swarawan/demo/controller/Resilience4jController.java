package id.swarawan.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class Resilience4jController {

    @Autowired
    private RestTemplate restApi;

    @Autowired
    private Resilience4JCircuitBreakerFactory cf;

    @GetMapping
    public ResponseEntity<String> getName() {
        CircuitBreaker circuitBreaker = cf.create("test-get-name");
        String result = circuitBreaker.run(
                () -> restApi.getForEntity("http://localhost:3001/api/name", String.class).getBody(),
                (throwable) -> throwable.getMessage()
        );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("error")
    public ResponseEntity<String> getNameError() {
        String result = restApi.getForEntity("http://localhost:3001/api/name", String.class).getBody();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
