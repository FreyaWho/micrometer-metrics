package icbc.com.micrometer.meter;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

//    1、不依赖于时间单位的记录值的测量，例如服务器有效负载值，缓存的命中率等。比如在查看 HTTP 服务响应时间的性能指标时，通常关注是的几个重要的百分比，如 50%，75%和 90%等。所关注的是对于这些百分比数量的请求都在多少时间内完成。
//分布概要根据每个事件所对应的值，把事件分配到对应的桶（bucket）中。
// Micrometer 默认的桶的值从 1 到最大的 long 值。可以通过 minimumExpectedValue 和 maximumExpectedValue 来控制值的范围。
// 如果事件所对应的值较小，可以通过 scale 来设置一个值来对数值进行放大。与分布概要密切相关的是直方图和百分比（percentile）。
//对于 Prometheus 这样本身提供了对百分比支持的监控系统，Micrometer 直接发送收集的直方图数据，由监控系统完成计算。
//对于其他不支持百分比的系统，Micrometer 会进行计算，并把百分比结果发送到监控系统。
public class ApplicationTestDistributionSummary {

    private SimpleMeterRegistry registry = new SimpleMeterRegistry();

    public void summary() {
        DistributionSummary summary = DistributionSummary.builder("simple")
                .description("simple distribution summary")
                .minimumExpectedValue(1L)
                .maximumExpectedValue(10L)
                .publishPercentiles(0.5, 0.75, 0.9)
                .register(registry);
        summary.record(1);
        summary.record(1.3);
        summary.record(2.4);
        summary.record(3.5);
        summary.record(4.1);
        System.out.println(summary.takeSnapshot());
    }

    public void test() {
        new ApplicationTestDistributionSummary().summary();
    }

}
