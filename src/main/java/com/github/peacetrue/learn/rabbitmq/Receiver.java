package com.github.peacetrue.learn.rabbitmq;

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
        //允许最大未确认的消息数，超过之后，将不再往相关消费者发送
        // qos：Quality of Service 服务质量，负载均衡
        channel.basicQos(2, false);
        channel.queueDeclare(CommonUtils.DEMO_QUEUE_NAME, true, false, false, null);
        channel.queueBind(CommonUtils.DEMO_QUEUE_NAME, CommonUtils.DEMO_EXCHANGE_NAME, CommonUtils.DEMO_BINDINGKEY_NAME);
        log.info(" [*] Waiting for messages. To exit press CTRL+C");

        //最开始往 2 个消费者发
        channel.basicConsume(CommonUtils.DEMO_QUEUE_NAME, false, new DefaultConsumer(channel) {
            //当这个消费者未确认数为 2 后，不给它发了
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("Received-1 '{}'", message);
                //ack 不确认，只能收到 2 条消息
                log.info("finished-1 '{}'", message);
            }
        });

        channel.basicConsume(CommonUtils.DEMO_QUEUE_NAME, false, new DefaultConsumer(channel) {
            //上面那个消费者接收 2 个之后，都往这发
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("Received-2 '{}'", message);
                //ack 不确认，只能收到 2 条消息
                channel.basicAck(envelope.getDeliveryTag(), false);
                log.info("finished-2 '{}'", message);
            }
        });

//        channel.close();
//        connection.close();
    }


}
