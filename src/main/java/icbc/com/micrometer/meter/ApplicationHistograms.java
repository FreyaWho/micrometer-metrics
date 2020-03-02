package icbc.com.micrometer.meter;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ApplicationHistograms {
    static SimpleMeterRegistry simpleMeterRegistry = new SimpleMeterRegistry();

    public void test() throws InterruptedException {
        Timer timer = Timer.builder("api-cost")
                .publishPercentileHistogram()
                .publishPercentiles(0.95,0.99)
                .register(simpleMeterRegistry);

        IntStream.rangeClosed(1,1000)
                .forEach(i -> {
                    timer.record(Duration.ofMillis(ThreadLocalRandom.current().nextInt(200)));
                    simpleMeterRegistry.getMeters()
                            .stream()
                            .forEach(m -> {
                                System.out.println(m.getId() + "-->" + m.measure());
                            });
                });
        TimeUnit.MINUTES.sleep(5);
    }
//    public void testHistogramGauges() throws InterruptedException {
//
//    }
}
