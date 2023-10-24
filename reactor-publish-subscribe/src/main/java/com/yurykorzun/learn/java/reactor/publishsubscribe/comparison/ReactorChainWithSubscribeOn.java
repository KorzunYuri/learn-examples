package com.yurykorzun.learn.java.reactor.publishsubscribe.comparison;

import com.yurykorzun.learn.java.reactor.publishsubscribe.ReactorChainProcessors;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * <link><a href="https://www.appsdeveloperblog.com/subscribeon-and-publishon-operators-in-project-reactor">example source</a></link>
 */
public class ReactorChainWithSubscribeOn {

    public static void main(String[] args) {
        Flux.just("New York", "London", "Paris", "Amsterdam")
                .map(ReactorChainProcessors::stringToUpperCase)
                .subscribeOn(Schedulers.boundedElastic())
                .filter(cityName -> cityName.length() <= 8)
                .map(ReactorChainProcessors::concat)
                .log()
            .collectList().block();
    }

}
