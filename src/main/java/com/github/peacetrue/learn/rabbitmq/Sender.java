package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author xiayx
 */
@Slf4j
public class Sender {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = CommonUtils.getConnectionFactory();
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {

            //声明死信交换机
            channel.exchangeDeclare(CommonUtils.DLX_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, null);
            channel.queueDeclare(CommonUtils.DLX_QUEUE_NAME, true, false, false, null);
            channel.queueBind(CommonUtils.DLX_QUEUE_NAME, CommonUtils.DLX_EXCHANGE_NAME, "");
            channel.basicConsume(CommonUtils.DLX_QUEUE_NAME, true, new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    log.info("处理死信:[{}]", new String(body));
                }
            });

            channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, null);
            channel.queueDeclare(CommonUtils.DEMO_QUEUE_NAME, true, true, true, CommonUtils.getDeadLetter());
            channel.queueBind(CommonUtils.DEMO_QUEUE_NAME, CommonUtils.DEMO_EXCHANGE_NAME, CommonUtils.DEMO_BINDINGKEY_NAME);
            while (true) {
                log.info("Waiting for message: ");
                //当前交换机路由不到队列，转到备胎
                String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                channel.basicPublish(CommonUtils.DEMO_EXCHANGE_NAME,
                        CommonUtils.DEMO_ROUTINGKEY_NAME,
                        new AMQP.BasicProperties().builder()
                                .deliveryMode(2)
                                .expiration("2")
                                .build(),
                        message.getBytes());
                log.info("Sent '{}'", message);
            }
        }
    }
}
