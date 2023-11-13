package com.yurykorzun.learn.java.multithreading.executors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorServicesExample {

    private record ValueBoundary(int lowerBound, int upperBound){};

    private record TestConfig (
            String name,
            ValueBoundary periodsNumber,
            ValueBoundary tasksNumber,
            ValueBoundary submitTimeoutMillis
    ) {
    };

    private record TestPeriod (
            int tasksNumber,
            int submitTimeout
    ){
    };

    private abstract static class BasicTask implements Runnable {

        @Getter @Setter
        static AtomicInteger tasksLeft = new AtomicInteger();

        abstract String getName();
        @SneakyThrows
        @Override
        public void run() {
            doSomething();
            tasksLeft.getAndDecrement();
        }

        abstract void doSomething() throws InterruptedException;

    }

    private static class ShortTask extends BasicTask {

        @Override
        String getName() {
            return "ShortTask (1-5 secs)";
        }

        @Override
        void doSomething() throws InterruptedException {
            Thread.sleep(new Random().nextInt(1000, 5000));
        }
    }

    private static class LongTask extends BasicTask {

        @Override
        String getName() {
            return "LongTask (5-30 secs)";
        }

        @Override
        void doSomething() throws InterruptedException {
            int sum = 0;
            Random random = new Random();
            Thread.sleep(random.nextInt(5000, 30000));
            for (int i = 0; i < random.nextInt(); i++) {
                sum++;
            }
        }
    }

    @AllArgsConstructor
    @Getter
    private static class ExecutorServiceDecorator {
        private String name;
        private ExecutorService service;
    }

    public static void main(String[] args) throws InterruptedException {

        //  emulate changing load: divide the test onto periods of different size, amount of tasks an submit frequency
        //  watch how number of threads is changing during the load
        List<ExecutorServiceDecorator>  executors   = new ArrayList<>();
        List<BasicTask>                 tasks       = new ArrayList<>();
        List<TestConfig>                configs     = new ArrayList<>();

        //  various periods settings
        ValueBoundary smallNumberOfPeriods          = new ValueBoundary(2,      3);
        //  tasks number settings
        ValueBoundary severalTasks                  = new ValueBoundary(2,      50);
        ValueBoundary manyTasks                     = new ValueBoundary(100,    1000);
        ValueBoundary unpredictableNumberOfTasks    = new ValueBoundary(2,      1000);
        //  submit frequency settings
        ValueBoundary frequentSubmitRate            = new ValueBoundary(1,      5);
        ValueBoundary unpredictableSubmitRate       = new ValueBoundary(1,      50);

        configs.add(new TestConfig("Low load", smallNumberOfPeriods, severalTasks, frequentSubmitRate));
        configs.add(new TestConfig("High load", smallNumberOfPeriods, manyTasks, frequentSubmitRate));
        configs.add(new TestConfig("Unpredictable load", smallNumberOfPeriods, unpredictableNumberOfTasks, unpredictableSubmitRate));

        tasks.add(new ShortTask());
        tasks.add(new LongTask());

        executors.add(new ExecutorServiceDecorator("Cached", Executors.newCachedThreadPool()));
        int processors = Runtime.getRuntime().availableProcessors();
        executors.add(new ExecutorServiceDecorator("Fixed", Executors.newFixedThreadPool(Math.max(processors - 2, 4))));
        executors.add(new ExecutorServiceDecorator("Single", Executors.newSingleThreadExecutor()));
        executors.add(new ExecutorServiceDecorator("Work-stealing", Executors.newWorkStealingPool()));

        for (ExecutorServiceDecorator executor : executors) {
            for (BasicTask task : tasks) {
                for (TestConfig config : configs) {
                    System.out.println("===============================");
                    System.out.printf("TESTING %s%n", executor.getName());
                    Thread.sleep(1000);
                    runTest(executor, task, config);
                    System.out.println("===============================");
                    Thread.sleep(1000);
                }
            }
        }
    }

    private static void runTest(ExecutorServiceDecorator executorDecorator, BasicTask task, TestConfig config) throws InterruptedException {

        final int monitorFrequencyMillis = 1000;

        //  predefine number of periods, number of tasks within a period and frequency rate bbased on provided bundaries
        List<TestPeriod> periods = new ArrayList<>();
        int tasksNumber = 0;
        Random random = new Random();
        for (int i = 0; i < random.nextInt(config.periodsNumber.lowerBound, config.periodsNumber.upperBound); i++) {
            TestPeriod period = new TestPeriod(
                    random.nextInt(config.tasksNumber.lowerBound, config.tasksNumber.upperBound)
                ,   random.nextInt(config.submitTimeoutMillis.lowerBound, config.submitTimeoutMillis.upperBound)
            );
            periods.add(period);
            tasksNumber += period.tasksNumber;
        }

        //  init tasks` countdown
        BasicTask.getTasksLeft().set(tasksNumber);

        ExecutorService executor = executorDecorator.getService();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (TestPeriod period : periods) {
                System.out.printf("New load period: %s tasks of type %s, submitting one per %s millis%n",
                        period.tasksNumber,
                        task.getName(),
                        period.submitTimeout
                );
                int targetTasksNumber = BasicTask.getTasksLeft().get() - period.tasksNumber;
                for (int j = 0; j < period.tasksNumber; j++) {
                    try {
                        Thread.sleep(period.submitTimeout);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    executor.submit(task);
                }
                while (BasicTask.getTasksLeft().get() > targetTasksNumber) {}
            }

        }).start();

        int workingThreads;
        while (BasicTask.getTasksLeft().get() > 0) {
            workingThreads = -1;
            if (executor instanceof ThreadPoolExecutor tpe) {
                workingThreads = tpe.getActiveCount();
            }
            System.out.printf("executor service %s is working, number of active threads: %s, service: %s%n"
                    , executorDecorator.getName()
                    , workingThreads > -1 ? workingThreads : "unknown"
                    , executor
            );
            Thread.sleep(monitorFrequencyMillis);
        }

    }

    private static void submitTasks(ExecutorService executorService, Runnable runnableTask, TestConfig config) throws InterruptedException {

    }
}
