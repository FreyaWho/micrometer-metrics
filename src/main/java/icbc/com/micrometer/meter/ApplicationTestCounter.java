package icbc.com.micrometer.meter;

import icbc.com.micrometer.classes.Order;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//counter总是增加的
//计数器（Counter）表示的是单个的只允许增加的值。通过 MeterRegistry 的 counter() 方法来创建表示计数器的 Counter 对象。
// 还可以使用 Counter.builder() 方法来创建 Counter 对象的构建器。
// Counter 所表示的计数值是 double 类型，其 increment() 方法可以指定增加的值。默认情况下增加的值是 1.0。
public class ApplicationTestCounter {
    //For test counters
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //构造方法一
//    static {
//        Metrics.addRegistry(new SimpleMeterRegistry());
//    }
    //构造方法二
    static MeterRegistry registry = new SimpleMeterRegistry();

    public void test() throws Exception {
        Order order1 = new Order();
        order1.setOrderId("ORDER_ID_1");
        order1.setAmount(100);
        order1.setChannel("CHANNEL_A");
        order1.setCreateTime(LocalDateTime.now());
        createOrder(order1);
        Order order2 = new Order();
        order2.setOrderId("ORDER_ID_2");
        order2.setAmount(200);
        order2.setChannel("CHANNEL_B");
        order2.setCreateTime(LocalDateTime.now());
        createOrder(order2);
        //构造方法一
//        Search.in(Metrics.globalRegistry).meters().forEach(each -> {
        Search.in(registry).meters().forEach(each -> {
        StringBuilder builder = new StringBuilder();
            builder.append("name:")
                    .append(each.getId().getName())
                    .append(",tags:")
                    .append(each.getId().getTags())
                    .append(",type:").append(each.getId().getType())
                    .append(",value:").append(each.measure());
            System.out.println(builder.toString());
        });
    }
    private static void createOrder(Order order) {
        //方法一：使用全局静态方法工厂类Metrics去构造
//        Metrics.counter("order.create",
//                "channel", order.getChannel(),
//                "createTime", FORMATTER.format(order.getCreateTime())).increment();

        //方法二：使用Counter接口提供了一个内部建造器类Counter.Builder去实例化Counter
        Counter counter = Counter.builder("order.create")  //名称
                .baseUnit("unit") //基础单位
                .description("desc") //描述
                .tags("channel", order.getChannel(),"cretaeTime", FORMATTER.format(order.getCreateTime()))  //标签
                .register(registry);//绑定的MeterRegistry
        counter.increment();

    }

}

