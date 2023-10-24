package com.yurykorzun.learn.java.reactor.publishsubscribe;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;

public class ReactorChainPublishOnAndSubscribeOn {
    public static void main(String[] args) throws InterruptedException {
        Scheduler publisher1 = Schedulers.newBoundedElastic(1, 10, "publisher1");
        Scheduler publisher2 = Schedulers.newBoundedElastic(1, 10, "publisher2");
        Scheduler subscriber1 = Schedulers.newBoundedElastic(1, 10, "subscriber1");
        Scheduler subscriber2 = Schedulers.newBoundedElastic(1, 10, "subscriber2");
        Scheduler subscriber3 = Schedulers.newBoundedElastic(1, 10, "subscriber3");

        String s = Mono.just("Hello reactor")
            .map(v -> logThread(v,"without thread management: will run on subscriber1 as the first known subscriber in chain"))
            .subscribeOn(subscriber1)
            .map(v -> logThread(v,"after subscribeOn(subscriber1): will run on subscriber1 as the first known subscriber in chain"))
            .subscribeOn(subscriber2)
            .map(v -> logThread(v,"after subscribeOn(subscriber2): will run on subscriber1 as the first known subscriber in chain"))
            .publishOn(publisher1)
            .map(v -> logThread(v,"after publishOn(publisher1): will run on publisher1 as the last publisher"))
            .publishOn(publisher2)
            .map(v -> logThread(v,"after publishOn(publisher2): will run on publisher2 as the last publisher"))
            .subscribeOn(subscriber3)
            .map(v -> logThread(v,"after subscribeOn(subscriber3): will run on publisher2 as the last publisher"))
        .block();

    }

    private static String logThread(String s, String msg){
        System.out.printf("thread %s: %s%n", Thread.currentThread().getName(), msg);
        return s;
    }

    private static void logThread(String msg){
        System.out.printf("thread %s: %s%n", Thread.currentThread().getName(), msg);
    }
}
