package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : xiayx
 * @since : 2020-09-12 10:08
 **/
public class CommonUtils {

    public final static String DEMO_EXCHANGE_NAME = "exchange-demo5";
    public final static String DEMO_QUEUE_NAME = "queue-demo5";
    public final static String DEMO_BINDINGKEY_NAME = "com.#.peacetrue";
    public final static String DEMO_ROUTINGKEY_NAME = "com.peacetrue";
    public final static String DLX_EXCHANGE_NAME = "exchange-DLX";
    public final static String DLX_QUEUE_NAME = "queue-DLX";
    public final static String DLX_ROUTINGKEY_NAME = "com.peacetrue";

    public static ConnectionFactory getConnectionFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.0.0.6");
        factory.setUsername("root");
        factory.setPassword("root123");
        factory.setVirtualHost("/");
        factory.setRequestedHeartbeat(Integer.MAX_VALUE);
        return factory;
    }

    public static Map<String, Object> getDeadLetter() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-expires", 60_000);//队列过期时间
        args.put("x-message-ttl", 60_000);//消息过期时间
        args.put("x-dead-letter-exchange", CommonUtils.DLX_EXCHANGE_NAME);
        args.put("x-dead-routing-key", CommonUtils.DLX_ROUTINGKEY_NAME);
        return args;
    }

}
