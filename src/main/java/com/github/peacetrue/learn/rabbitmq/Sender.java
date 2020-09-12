package com.github.peacetrue.learn.rabbitmq;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiayx
 */
@Slf4j
public class Sender {

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = CommonUtils.getConnectionFactory();
        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            //声明备胎交换机
            channel.exchangeDeclare(CommonUtils.AE_EXCHANGE_NAME, BuiltinExchangeType.FANOUT, true, false, null);
            channel.queueDeclare(CommonUtils.AE_QUEUE_NAME, true, false, false, null);
            channel.queueBind(CommonUtils.AE_QUEUE_NAME, CommonUtils.AE_EXCHANGE_NAME, "");
            channel.basicConsume(CommonUtils.AE_QUEUE_NAME, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    log.info("handleDelivery:[{}]", new String(body));
                }
            });

            //rabbitmqctl set_policy AE "^normalExchange$" ‘{"alternate-exchange": "myAE"}’
            Map<String, Object> args = new HashMap<>();
            args.put("alternate-exchange", CommonUtils.AE_EXCHANGE_NAME);
            channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, args);
            while (true) {
                log.info("Waiting for message: ");
                //当前交换机路由不到队列，转到备胎
                String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                channel.basicPublish(CommonUtils.DEMO_EXCHANGE_NAME,
                        CommonUtils.DEMO_ROUTINGKEY_NAME + ".cannotRouted",
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes());
                log.info("Sent '{}'", message);
            }
        }
    }
}
