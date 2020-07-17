package com.zq.provider;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.FutureTask;

public class KafkaProvider{
    public static void main(String[] args) throws Exception{

        Properties props = new Properties();
        props.put("bootstrap.servers", "izwz91ypsiojldqhida6jyz:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 10240);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 102400);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for(int i = 0; i < 100; i++)
            producer.send(new ProducerRecord<String, String>("my-topic", Integer.toString(i), Integer.toString(i)));

        producer.close();
    }
}
