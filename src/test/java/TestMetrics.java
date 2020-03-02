import icbc.com.micrometer.meter.ApplicationGauge;
import org.junit.Test;

public class TestMetrics {
    @Test
    public void testGauge() throws InterruptedException {
        ApplicationGauge demo = new ApplicationGauge();
        demo.test();
    }


}
