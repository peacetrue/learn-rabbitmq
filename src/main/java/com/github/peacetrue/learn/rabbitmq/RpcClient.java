package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @author : xiayx
 * @since : 2020-09-12 21:07
 **/
@Slf4j
public class RpcClient {

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private Channel channel;
    private String replyToQueue;
    private Map<String, Consumer<String>> consumers = new ConcurrentHashMap<>();

    public RpcClient(Channel channel) throws Exception {
        this.channel = channel;
        channel.queueDeclare("rpc_queue", false, false, false, null);
        this.replyToQueue = channel.queueDeclare().getQueue();
        channel.basicConsume(replyToQueue, true, new DefaultConsumer(channel) {
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String key = properties.getCorrelationId();
                log.info("response-{}:[{}]", key, new String(body));
                Optional.ofNullable(consumers.remove(key)).ifPresent(consumer -> consumer.accept(new String(body)));
            }
        });
    }

    public void invoke(String message, Consumer<String> consumer) {
        String key = "key-" + atomicInteger.getAndIncrement();
        log.info("request-{}:[{}]", key, message);
        consumers.put(key, consumer);
        try {
            channel.basicPublish("",
                    "rpc_queue",
                    new AMQP.BasicProperties.Builder()
                            .correlationId(key)
                            .replyTo(replyToQueue)
                            .build(),
                    message.getBytes());
        } catch (IOException e) {
            log.error("error", e);
        }
    }

    public static void main(String[] args) throws Exception {
        Channel channel = CommonUtils.getConnectionFactory().newConnection().createChannel();
        RpcClient rpcClient = new RpcClient(channel);
        IntStream.range(0, 100).forEach(i ->
                CompletableFuture.runAsync(() -> rpcClient.invoke(i + "-", null))
        );
    }
}
