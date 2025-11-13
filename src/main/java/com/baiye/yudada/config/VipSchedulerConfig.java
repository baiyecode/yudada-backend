package com.baiye.yudada.config;


import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@Data
public class VipSchedulerConfig {

    @Bean
    public Scheduler vipScheduler() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "VIPThreadPool-" + threadNumber.getAndIncrement());
                t.setDaemon(false); // 设置为非守护线程,有任务就不会退出，保证任务可以执行完
                return t;
            }
        };

        ExecutorService executorService = Executors.newScheduledThreadPool(10, threadFactory);

        //调用 Schedulers.from() 静态方法，将刚才创建的 Java ExecutorService 包装成一个 RxJava Scheduler。
        //这个包装后的 Scheduler 可以被 RxJava 的 Observable 链使用（例如通过 observeOn() 或 subscribeOn()），
        // 使得 RxJava 的操作符或数据流处理逻辑在这个专门的线程池中执行。
        return Schedulers.from(executorService);
    }
}
