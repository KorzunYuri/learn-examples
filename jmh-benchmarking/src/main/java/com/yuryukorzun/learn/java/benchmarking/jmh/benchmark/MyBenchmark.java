package com.yuryukorzun.learn.java.benchmarking.jmh.benchmark;

public class MyBenchmark {

    public void doNothing() {
    }

    public void createUnusedObject() {
        new Object();
    }

    public Object createAndReturnObject() {
        return new Object();
    }

}
