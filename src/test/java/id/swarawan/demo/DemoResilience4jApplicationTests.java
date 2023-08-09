package id.swarawan.demo;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DemoResilience4jApplicationTests {

//    private SimpleDateFormat simpleDateFormat = DateFormat.getDateTimeInstance().form

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Test
    void contextLoads() {
        String stringDate = "2023-02-10 15:30:15";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(stringDate);
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    TemporalAccessor temporalAccessor = dateTimeFormatter.parse(stringDate);
//                    System.out.println("Parsed date: " + temporalAccessor.);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            executorService.submit(runnable);
        }
        executorService.shutdown();
    }

}
