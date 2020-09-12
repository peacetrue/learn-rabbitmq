package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.*;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.UnaryOperator;

/**
 * @author : xiayx
 * @since : 2020-09-12 21:02
 **/
@Slf4j
@Setter
public class RpcServer {

    private ConnectionFactory factory;
    private UnaryOperator<String> consumer = s -> s + "-reply";

    public void start() throws Exception {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);
        channel.queueDeclare("rpc_queue", false, false, false, null);
        channel.basicConsume("rpc_queue", false, new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                log.info("Received: [{}]", message);
                message = consumer.apply(message);
                AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                        .correlationId(properties.getCorrelationId())
                        .build();
                channel.basicPublish("", properties.getReplyTo(), props, message.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

    public static void main(String[] args) throws Exception {
        RpcServer rpcServer = new RpcServer();
        rpcServer.setFactory(CommonUtils.getConnectionFactory());
        rpcServer.start();
    }
}
