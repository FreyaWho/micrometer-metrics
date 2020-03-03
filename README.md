# micrometer-metrics
##MeterRegistry

MeterRegistry在Micrometer是一个抽象类，主要实现包括：

    1、SimpleMeterRegistry：每个Meter的最新数据可以收集到SimpleMeterRegistry实例中，但是这些数据不会发布到其他系统，也就是数据是位于应用的内存中的。
    2、CompositeMeterRegistry：多个MeterRegistry聚合，内部维护了一个MeterRegistry的列表。
    3、全局的MeterRegistry：工厂类io.micrometer.core.instrument.Metrics中持有一个静态final的CompositeMeterRegistry实例globalRegistry。

当然，使用者也可以自行继承MeterRegistry去实现自定义的MeterRegistry。SimpleMeterRegistry适合做调试的时候使用，它的简单使用方式如下：
```
MeterRegistry registry = new SimpleMeterRegistry();
Counter counter = registry.counter("counter");
counter.increment();
```

全局的MeterRegistry的使用方式更加简单便捷，因为一切只需要操作工厂类Metrics的静态方法：
```
Metrics.addRegistry(new SimpleMeterRegistry());
Counter counter = Metrics.counter("counter", "tag-1", "tag-2");
counter.increment();
```

Counter

Counter是一种比较简单的Meter，它是一种单值的度量类型，或者说是一个单值计数器。Counter接口允许使用者使用一个固定值(必须为正数)进行计数。准确来说：Counter就是一个增量为正数的单值计数器。这个举个很简单的使用例子：

1
2
3
4

	

MeterRegistry meterRegistry = new SimpleMeterRegistry();
Counter counter = meterRegistry.counter("http.request", "createOrder", "/order/create");
counter.increment();
System.out.println(counter.measure()); // [Measurement{statistic='COUNT', value=1.0}]

使用场景：

Counter的作用是记录XXX的总量或者计数值，适用于一些增长类型的统计，例如下单、支付次数、Http请求总量记录等等，通过Tag可以区分不同的场景，对于下单，可以使用不同的Tag标记不同的业务来源或者是按日期划分，对于Http请求总量记录，可以使用Tag区分不同的URL。用下单业务举个例子：


Timer

Timer(计时器)适用于记录耗时比较短的事件的执行时间，通过时间分布展示事件的序列和发生频率。所有的Timer的实现至少记录了发生的事件的数量和这些事件的总耗时，从而生成一个时间序列。Timer的基本单位基于服务端的指标而定，但是实际上我们不需要过于关注Timer的基本单位，因为Micrometer在存储生成的时间序列的时候会自动选择适当的基本单位。Timer接口提供的常用方法如下：

使用场景：

根据个人经验和实践，总结如下：

    1、记录指定方法的执行时间用于展示。
    2、记录一些任务的执行时间，从而确定某些数据来源的速率，例如消息队列消息的消费速率等
    
Gauge

Gauge(仪表)是获取当前度量记录值的句柄，也就是它表示一个可以任意上下浮动的单数值度量Meter。Gauge通常用于变动的测量值，测量值用ToDoubleFunction参数的返回值设置，如当前的内存使用情况，同时也可以测量上下移动的”计数”，比如队列中的消息数量。官网文档中提到Gauge的典型使用场景是用于测量集合或映射的大小或运行状态中的线程数。Gauge一般用于监测有自然上界的事件或者任务，而Counter一般使用于无自然上界的事件或者任务的监测，所以像Http请求总量计数应该使用Counter而非Gauge。MeterRegistry中提供了一些便于构建用于观察数值、函数、集合和映射的Gauge相关的方法：

1
2
3

	

List<String> list = registry.gauge("listGauge", Collections.emptyList(), new ArrayList<>(), List::size); 
List<String> list2 = registry.gaugeCollectionSize("listSize2", Tags.empty(), new ArrayList<>()); 
Map<String, Integer> map = registry.gaugeMapSize("mapGauge", Tags.empty(), new HashMap<>());

上面的三个方法通过MeterRegistry构建Gauge并且返回了集合或者映射实例，使用这些集合或者映射实例就能在其size变化过程中记录这个变更值。更重要的优点是，我们不需要感知Gauge接口的存在，只需要像平时一样使用集合或者映射实例就可以了。此外，Gauge还支持java.lang.Number的子类，java.util.concurrent.atomic包中的AtomicInteger和AtomicLong，还有Guava提供的AtomicDouble：

1
2
3

	

AtomicInteger n = registry.gauge("numberGauge", new AtomicInteger(0));
n.set(1);
n.set(2);

除了使用MeterRegistry创建Gauge之外，还可以使用建造器流式创建：

1
2
3
4
5
6

	

//一般我们不需要操作Gauge实例
Gauge gauge = Gauge
    .builder("gauge", myObj, myObj::gaugeValue)
    .description("a description of what this gauge does") // 可选
    .tags("region", "test") // 可选
    .register(registry);

使用场景：

根据个人经验和实践，总结如下：

    1、有自然(物理)上界的浮动值的监测，例如物理内存、集合、映射、数值等。
    2、有逻辑上界的浮动值的监测，例如积压的消息、(线程池中)积压的任务等，其实本质也是集合或者映射的监测。

举个相对实际的例子，假设我们需要对登录后的用户发送一条短信或者推送，做法是消息先投放到一个阻塞队列，再由一个线程消费消息进行其他操作：

DistributionSummary

Summary(摘要)主要用于跟踪事件的分布，在Micrometer中，对应的类是DistributionSummary(分发摘要)。它的使用方式和Timer十分相似，但是它的记录值并不依赖于时间单位。常见的使用场景：使用DistributionSummary测量命中服务器的请求的有效负载大小。使用MeterRegistry创建DistributionSummary实例如下：

使用场景：

根据个人经验和实践，总结如下：

    1、不依赖于时间单位的记录值的测量，例如服务器有效负载值，缓存的命中率等。
