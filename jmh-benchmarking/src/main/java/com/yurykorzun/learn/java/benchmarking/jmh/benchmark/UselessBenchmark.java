package com.yurykorzun.learn.java.benchmarking.jmh.benchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 *  simple demonstration of dead code elimination problem and a proper workaround for it
 *  method to measure is producing a single unised instance of <class>java.lang.Object</class>
 */
public class UselessBenchmark extends MyBenchmark {

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Timeout(time = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void singleUselessBenchmark() {
        doNothing();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Timeout(time = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void multiUselessBenchmark() {
        for (int i = 0; i < 100L; i++) {
            doNothing();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Timeout(time = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void stillUselessBenchmark() {
        for (int i = 0; i < 100L; i++) {
            createUnusedObject();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Timeout(time = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void ratherUselessBenchmark() {
        for (int i = 0; i < 100L; i++) {
            Object obj = createAndReturnObject();
        }
    }

}
