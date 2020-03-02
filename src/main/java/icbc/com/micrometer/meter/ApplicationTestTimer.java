package icbc.com.micrometer.meter;

import icbc.com.micrometer.classes.Order;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ApplicationTestTimer {
//1、记录指定方法的执行时间用于展示。
//2、记录一些任务的执行时间，从而确定某些数据来源的速率，例如消息队列消息的消费速率等。
    //实际生产环境中，可以通过spring-aop把记录方法耗时的逻辑抽象到一个切面中，这样就能减少不必要的冗余的模板代码。

    private static final Random R = new Random();

    static {
        Metrics.addRegistry(new SimpleMeterRegistry());
    }

    public void test() {
        Order order1= new Order();
        order1.setOrderId("ORDER_1");
        order1.setAmount(100);
        order1.setChannel("CHANNLE_A");
        order1.setCreateTime(LocalDateTime.now());
        //方法一：使用Metrics构造TImer
//        Timer timer=Metrics.timer("timer","createOrder","cost");

        //方法二，使用Builder
        MeterRegistry registry = new SimpleMeterRegistry();
        Timer timer = Timer.builder("timer").description("use builder to bulid Timer").tag("Channel",order1.getChannel()).register(registry);
        timer.record(()->creteOrder(order1));
        System.out.println("name:"+timer.getId().getName()+"tags:"+timer.getId().getTags()+"duration:"+timer.measure());

        //基于Timer内部类TImer.Sample的start和stop方法记录两者之间的逻辑的执行耗时
        Timer.Sample sample = Timer.start(registry);
        //业务逻辑
        Order order2=new Order();
        order2.setOrderId("ORDER_2");
        order2.setAmount(60);
        order2.setChannel("CHANNLE_B");
        order2.setCreateTime(LocalDateTime.now());

        sample.stop(registry.timer("sample timer","channel",order2.getChannel()));


    }
    private static void creteOrder(Order order){
        try {
            TimeUnit.SECONDS.sleep(R.nextInt(5));
        }catch (InterruptedException e){
            //
        }
    }
}
