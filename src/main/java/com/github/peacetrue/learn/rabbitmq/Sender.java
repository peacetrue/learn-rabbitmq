package com.github.peacetrue.learn.rabbitmq;

import com.github.peacetrue.spring.util.BeanUtils;
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
            channel.exchangeDeclare(CommonUtils.DEMO_EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, true, null);
            //路由不到匹配队列
            channel.addReturnListener(returnMessage -> {
                log.info("returnMessage:[{}]", BeanUtils.map(returnMessage));
            });
            while (true) {
                log.info("Waiting for message: ");
                //RabbitMQ 3 .0 版本开始去掉了对该参数的支持（官方解释是immediate标记会影响镜像队列性能，增加代码复杂性，并建议采用"设置消息TTL"和"DLX"等方式替代，下一节将会介绍），如果将immediate设为true，则发送消息时会发生如下错误
                String message = new BufferedReader(new InputStreamReader(System.in)).readLine();
                channel.basicPublish(CommonUtils.DEMO_EXCHANGE_NAME,
                        CommonUtils.DEMO_ROUTINGKEY_NAME,
                        true,
                        true,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes());
                log.info("Sent '{}'", message);
            }
        }
    }
}
