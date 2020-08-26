package com.zq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.support.Acknowledgment;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class BaseConsumer implements ApplicationListener<ConsumerStoppedEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(BaseConsumer.class);

    //@Value("${kafka.consumer.thread.min}")
    private int consumerThreadMin = 2;

    //@Value("${kafka.consumer.thread.max}")
    private int consumerThreadMax = 2;

    private ThreadPoolExecutor consumeExecutor;

    private volatile boolean isClosePoolExecutor = false;

    @PostConstruct
    public void init() {

        this.consumeExecutor = new ThreadPoolExecutor(
                consumerThreadMin,
                consumerThreadMax,
                1000 * 60,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>());
    }

    /**
     * 收到spring-kafka 关闭Consumer的通知
     * @param event 关闭Consumer 事件
     */
    @Override
    public void onApplicationEvent(ConsumerStoppedEvent event) {

        isClosePoolExecutor = true;
        closeConsumeExecutorService();

    }

    private void closeConsumeExecutorService() {

        if (!consumeExecutor.isShutdown()) {

            ThreadUtil.shutdownGracefully(consumeExecutor, 120, TimeUnit.SECONDS);
            LOG.info("consumeExecutor stopped");

        }

    }

    @PreDestroy
    public void doClose() {
        if (!isClosePoolExecutor) {
            closeConsumeExecutorService();
        }
    }

    @KafkaListener(topics = "${msg.consumer.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void onMessage(List<String> msgList, Acknowledgment ack) {

        CountDownLatch countDownLatch = new CountDownLatch(msgList.size());

        for (String message : msgList) {
            submitConsumeTask(message, countDownLatch);
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOG.error("countDownLatch exception ", e);
        }

        // 本次批量消费完,手动提交
        ack.acknowledge();
        LOG.info("finish commit offset");

    }

    private void submitConsumeTask(String message, CountDownLatch countDownLatch) {
        consumeExecutor.submit(() -> {
            try {
                onDealMessage(message);
            } catch (Exception ex) {
                LOG.error("on DealMessage exception:", ex);
            } finally {
                countDownLatch.countDown();
            }
        });
    }

    /**
     * 子类实现该抽象方法处理具体消息的业务逻辑
     * @param message kafka的消息
     */
    protected abstract void onDealMessage(String message);

}
