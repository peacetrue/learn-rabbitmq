package com.github.peacetrue.learn.rabbitmq;

//代码（在Recv.java中）与Send的导入几乎相同：

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class Receiver {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = CommonUtils.getConnectionFactory();
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(CommonUtils.DEMO_QUEUE_NAME, true, false, false, null);
        channel.queueBind(CommonUtils.DEMO_QUEUE_NAME, CommonUtils.DEMO_EXCHANGE_NAME, CommonUtils.DEMO_ROUTINGKEY_NAME);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        channel.basicConsume(CommonUtils.DEMO_QUEUE_NAME, new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                System.out.println(" [x] Received '" + message + "'");
            }
        });

//        channel.close();
//        connection.close();
    }


}
