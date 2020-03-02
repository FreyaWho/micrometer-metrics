package icbc.com.micrometer.meter;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

//如果一个任务的耗时很长，直接使用 Timer 并不是一个好的选择
// 因为 Timer 只有在任务完成之后才会记录时间。
// 更好的选择是使用 LongTaskTimer。LongTaskTimer 可以在任务进行中记录已经耗费的时间
public class ApplicationLongTaskTimer {
    public void test() {


        MeterRegistry registry = new SimpleMeterRegistry();
        LongTaskTimer timer = registry.more().longTaskTimer("long");
        timer.record(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}