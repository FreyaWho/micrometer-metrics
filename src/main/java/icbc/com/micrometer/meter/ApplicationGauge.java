package icbc.com.micrometer.meter;//Gauge(仪表)是获取当前度量记录值的句柄，也就是它表示一个可以任意上下浮动的单数值度量Meter。
// Gauge通常用于变动的测量值，测量值用ToDoubleFunction参数的返回值设置，如当前的内存使用情况，
// 同时也可以测量上下移动的”计数”，比如队列中的消息数量。
// 官网文档中提到Gauge的典型使用场景是用于测量集合或映射的大小或运行状态中的线程数。
// Gauge一般用于监测有自然上界的事件或者任务，而Counter一般使用于无自然上界的事件或者任务的监测，所以像Http请求总量计数应该使用Counter而非Gauge。

import icbc.com.micrometer.classes.Messsage;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

//使用场景：
//
//根据个人经验和实践，总结如下：
//
//    1、有自然(物理)上界的浮动值的监测，例如物理内存、集合、映射、数值等。
//    2、有逻辑上界的浮动值的监测，例如积压的消息、(线程池中)积压的任务等，其实本质也是集合或者映射的监测。
public class ApplicationGauge {
    private static final MeterRegistry METER_REGISTRY = new SimpleMeterRegistry();
    private final static BlockingQueue<Messsage> QUEUE = new ArrayBlockingQueue<>(500);
    private static BlockingQueue<Messsage> REAL_QUEUE;
    private static MeterRegistry registry = new SimpleMeterRegistry();

    static {
        REAL_QUEUE = METER_REGISTRY.gauge("message-gauge",QUEUE, Collection::size);
    }

    public void test() throws InterruptedException {
        consume();
        Messsage messsage1 = new Messsage();
        messsage1.setUserId(1);
        messsage1.setContent("user1 content");

        Messsage messsage2 = new Messsage();
        messsage2.setUserId(2);
        messsage2.setContent("user2 content");

        REAL_QUEUE.put(messsage1);
        REAL_QUEUE.put(messsage2);


        AtomicInteger value = registry.gauge("gauge1", new AtomicInteger(0));
        value.set(1);

        List<String> list = registry.gaugeCollectionSize("list.size", Collections.emptyList(), new ArrayList<>());
        list.add("a");

        Map<String, String> map = registry.gaugeMapSize("map.size", Collections.emptyList(), new HashMap<>());
        map.put("a", "b");

//        Gauge.builder("value", this, Gauges::getValue)
//                .description("a simple gauge")
//                .tag("tag1", "a")
//                .register(registry);
    }

    private static void consume(){
        new Thread(() ->{
            while (true){
                try{
                    Messsage messsage=REAL_QUEUE.take();
                    System.out.println(messsage.getUserId());
                }catch (InterruptedException e){
                    //
                }
            }
        }
        ).start();
    }

}
