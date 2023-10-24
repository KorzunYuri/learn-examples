package com.yurykorzun.learn.java.reactor.publishsubscribe.comparison;

import com.yurykorzun.learn.java.reactor.publishsubscribe.ReactorChainProcessors;
import reactor.core.publisher.Flux;

/**
 * <link><a href="https://www.appsdeveloperblog.com/subscribeon-and-publishon-operators-in-project-reactor">example source</a></link>
 */
public class ReactorChainWithoutContextManagement {

    public static void main(String[] args) {
        Flux<String> cities = Flux.just("New York", "London", "Paris", "Amsterdam")
                .map(ReactorChainProcessors::stringToUpperCase)
                .filter(cityName -> cityName.length() <= 8)
                .map(ReactorChainProcessors::concat)
                .log();
        cities.subscribe();
    }
}
