package com.paddi.util;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Paddi-Yan
 * @Project: paddi-bilibili-server
 * @CreatedTime: 2023年06月17日 20:03:50
 */
public class RocketMQUtils {

    public static void sendMessageSync(DefaultMQProducer producer, Message message) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        SendResult result = producer.send(message);
        System.out.println(result);
    }

    public static void sendMessageAsync(DefaultMQProducer producer, Message message) throws RemotingException, InterruptedException, MQClientException {
        int messageCount = 2;
        CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
        for(int i = 0; i < messageCount; i++) {
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId() + "consumer successfully!");
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch.countDown();
                    System.out.println("send or consumer message exception! " + e);
                }
            });
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
    }

}
