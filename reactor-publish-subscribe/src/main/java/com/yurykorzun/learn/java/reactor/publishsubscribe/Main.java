package com.yurykorzun.learn.java.reactor.publishsubscribe;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scheduler scheduler1 = Schedulers.boundedElastic();
        Scheduler scheduler2 = Schedulers.boundedElastic();
        scheduler1.schedule(() -> logThread("I`m scheduler1"), 1, TimeUnit.SECONDS);
        scheduler2.schedule(() -> logThread("And I`m scheduler2"), 2, TimeUnit.SECONDS);
        scheduler1.start();
        scheduler2.start();

        Thread.sleep(3000);

        String s = Mono.just("Hello reactor")
            .map(v -> logThread(v,"without thread management"))
            .publishOn(scheduler1)
            .map(v -> logThread(v,"published on scheduler1: first step"))
            .map(v -> logThread(v,"published on scheduler1: second step"))
            .map(v -> logThread(v,"published on scheduler1: third step"))
            .subscribeOn(scheduler2)
            .map(v -> logThread(v,"subscribed on scheduler2: first step"))
            .map(v -> logThread(v,"subscribed on scheduler2: second step"))
        .block();
        logThread("resulted object: " + s);

    }

    private static String logThread(String s, String msg){
        System.out.printf("thread %s: %s%n", Thread.currentThread().getName(), msg);
        return s;
    }

    private static void logThread(String msg){
        System.out.printf("thread %s: %s%n", Thread.currentThread().getName(), msg);
    }
}
