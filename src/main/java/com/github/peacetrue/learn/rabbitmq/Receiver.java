package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Receiver {

    public final static ExecutorService executor = Executors.newCachedThreadPool();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = CommonUtils.getConnectionFactory();
        main(factory);
    }

    public static void main(ConnectionFactory factory) throws IOException, TimeoutException {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //允许最大未确认的消息数，超过之后，将不再往相关消费者发送
        // qos：Quality of Service 服务质量，负载均衡
        channel.basicQos(2, false);
        //自动删除交换机，客户端也需要声明，防止不存在异常
        channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, null);
        //独占模式，持久属性无用
        channel.queueDeclare(CommonUtils.DEMO_QUEUE_NAME, true, true, true, null);
        channel.queueBind(CommonUtils.DEMO_QUEUE_NAME, CommonUtils.DEMO_EXCHANGE_NAME, CommonUtils.DEMO_BINDINGKEY_NAME);
        log.info(" [*] Waiting for messages. To exit press CTRL+C");

        channel.basicConsume(CommonUtils.DEMO_QUEUE_NAME, false, new DefaultConsumer(channel) {
            //上面那个消费者接收 2 个之后，都往这发
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("Received-1 '{}'", message);
                //重入队列，没有其他消费者，死循环
                channel.basicAck(envelope.getDeliveryTag(), true);
            }
        });
    }


}
