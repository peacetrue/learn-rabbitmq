package com.github.peacetrue.learn.rabbitmq;
//In Send.java, we need some classes imported:

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author xiayx
 */
@Slf4j
public class Sender {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = CommonUtils.getConnectionFactory();
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, "direct", true, false, null);
            while (true) {
                log.info("Waiting for message: ");
                String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                channel.basicPublish(CommonUtils.DEMO_EXCHANGE_NAME,
                        CommonUtils.DEMO_ROUTINGKEY_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes());
                log.info("Sent '{}'", message);
            }
        }
    }
}
