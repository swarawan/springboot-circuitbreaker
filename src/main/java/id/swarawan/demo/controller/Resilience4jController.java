package id.swarawan.demo.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerConfigurationProperties;
import io.github.resilience4j.springboot3.circuitbreaker.monitoring.health.CircuitBreakersHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.StatusAggregator;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class Resilience4jController {

    @Autowired
    private RestTemplate restApi;

    @Autowired
    private Resilience4JCircuitBreakerFactory cf;

    @Autowired
    private CircuitBreakerConfigurationProperties configurationProperties;

    @Autowired
    private StatusAggregator statusAggregator;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping(value = "/app")
    public ResponseEntity<String> app() {
        String result;
        String cbName = "test-app";

        CircuitBreaker circuitBreaker = cf.create(cbName);
        result = circuitBreaker.run(
                () -> restApi.getForEntity("http://localhost:3001/api/name", String.class).getBody(),
                (throwable) -> throwable.getLocalizedMessage()
        );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/error")
    public ResponseEntity<String> getNameError() {
        String result = restApi.getForEntity("http://localhost:3001/api/name", String.class).getBody();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/health")
    public ResponseEntity<Map<String, Object>> health() {
        CircuitBreakersHealthIndicator healthIndicator = new CircuitBreakersHealthIndicator(circuitBreakerRegistry, configurationProperties, statusAggregator);
        Map<String, Object> cbHealth = healthIndicator.health().getDetails();
        return new ResponseEntity<>(cbHealth, HttpStatus.OK);
    }

    private boolean isCircuitClosed(String name) {
        CircuitBreakersHealthIndicator healthIndicator = new CircuitBreakersHealthIndicator(circuitBreakerRegistry, configurationProperties, statusAggregator);
        Map<String, Object> cbHealth = healthIndicator.health().getDetails();
        for (Map.Entry<String, Object> entry : cbHealth.entrySet()) {
            if (entry.getKey().equals(name)) {
                Health value = (Health) entry.getValue();
                return value.getStatus().toString().equals("UP")
                        || value.getStatus().toString().equals("CIRCUIT_HALF_OPEN");
            }
        }
        return true;
    }
}
