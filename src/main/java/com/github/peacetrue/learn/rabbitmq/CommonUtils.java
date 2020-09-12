package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

/**
 * @author : xiayx
 * @since : 2020-09-12 10:08
 **/
public class CommonUtils {

    public final static String DEMO_EXCHANGE_NAME = "exchange-demo4";
    public final static String DEMO_QUEUE_NAME = "queue-demo4";
    public final static String DEMO_BINDINGKEY_NAME = "com.#.peacetrue";
    public final static String DEMO_ROUTINGKEY_NAME = "com.peacetrue";
    public final static String AE_EXCHANGE_NAME = "exchange-AE";
    public final static String AE_QUEUE_NAME = "queue-AE";

    public static ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.0.0.6");
        factory.setUsername("root");
        factory.setPassword("root123");
        factory.setVirtualHost("/");
        factory.setRequestedHeartbeat(Integer.MAX_VALUE);
        return factory;
    }

}
