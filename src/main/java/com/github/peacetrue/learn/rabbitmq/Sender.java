package com.github.peacetrue.learn.rabbitmq;
//In Send.java, we need some classes imported:

import com.rabbitmq.client.*;
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
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            //声明持久化自动删除交换机，从管理界面可以看到交换机存在
            //使用 Receiver 连上交换机，然后再断开，从管理界面再看交换机，就不存在了
            channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, null);
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
