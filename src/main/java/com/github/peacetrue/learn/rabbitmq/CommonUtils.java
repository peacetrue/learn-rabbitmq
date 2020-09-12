package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author : xiayx
 * @since : 2020-09-12 10:08
 **/
public class CommonUtils {

    public final static String DEMO_QUEUE_NAME = "queue-demo2";
    public final static String DEMO_EXCHANGE_NAME = "exchange-demo2";
    public final static String DEMO_BINDINGKEY_NAME = "com.#.peacetrue";
    public final static String DEMO_ROUTINGKEY_NAME = "com.peacetrue";

    public static ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.0.0.6");
        factory.setUsername("root");
        factory.setPassword("root123");
        factory.setVirtualHost("/");
        return factory;
    }

}
