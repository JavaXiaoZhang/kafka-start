package com.zq.consumer;

import org.springframework.stereotype.Component;

@Component
public class ConsumerImpl extends BaseConsumer{
    @Override
    protected void onDealMessage(String message) {
        System.out.println("收到：--- "+message);
    }
}
