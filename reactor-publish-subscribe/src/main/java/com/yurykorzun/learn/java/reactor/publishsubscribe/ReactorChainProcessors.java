package com.yurykorzun.learn.java.reactor.publishsubscribe;

public class ReactorChainProcessors {

    public static String stringToUpperCase(String name) {
        System.out.printf("%s stringToUpperCase%n", Thread.currentThread().getName());
        return name.toUpperCase();
    }

    public static String concat(String name) {
        System.out.printf("%s concat%n", Thread.currentThread().getName());
        return name.concat(" City");
    }

}
