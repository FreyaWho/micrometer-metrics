import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

//计量器注册表MeterRegister
//每个监控系统有自己独有的计量器注册表实现。
// 模块 micrometer-core 中提供的类 SimpleMeterRegistry 是一个基于内存的计量器注册表实现。
// SimpleMeterRegistry 不支持导出数据到监控系统，主要用来进行本地开发和测试。
public class RegistryTest {
    //Micrometer中包含一个SimpleMeterRegistry，它在内存中维护每个meter的最新值，并且不将数据导出到任何地方。如果你还没有一个首选的监测系统，你可以先用SimpleMeterRegistry：
    MeterRegistry registry = new SimpleMeterRegistry();
    //registry通用标签
//    registry.config().commonTags("tag1", "a", "tag2", "b");

    //method 1
    Counter counter = registry.counter("counter");
    //method 2
     Counter counter2 =  Counter.builder("counter").baseUnit("beans").description("a description of what this counter does").tags("region","test").register(registry);

//    Micrometer 本身提供了一个静态的全局计量器注册表对象 Metrics.globalRegistry。
//    该注册表是一个组合注册表。
//    使用 Metrics 类中的静态方法创建的计量器，都会被添加到该全局注册表中。
//    Metrics.addRegistry() 方法直接在全局注册表对象中添加新的注册表对象，而 Metrics.counter() 方法创建的计数器自动添加到全局注册表中。
public void test() {
    Metrics.addRegistry(new SimpleMeterRegistry());
    Counter counter = Metrics.counter("simple");
    counter.increment();
}
}
