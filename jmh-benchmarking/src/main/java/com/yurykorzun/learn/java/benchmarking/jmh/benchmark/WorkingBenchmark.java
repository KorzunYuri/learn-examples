package com.yurykorzun.learn.java.benchmarking.jmh.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

public class WorkingBenchmark extends MyBenchmark{

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Fork(value = 1, warmups = 2)
    @Timeout(time = 1)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void actualBenchmark(Blackhole blackhole) {
        blackhole.consume(createAndReturnObject());
    }

}
