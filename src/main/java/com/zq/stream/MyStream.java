package com.zq.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.util.Arrays;
import java.util.Properties;

//@EnableKafkaStreams
//@Configuration
public class MyStream {

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "wordcount-application");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "izwz91ypsiojldqhida6jyz:9092");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> textLines = builder.stream("my-filter");
        KStream<String, String> filter = textLines.filter(new Predicate<String, String>() {
            @Override
            public boolean test(String key, String value) {
                if (Integer.valueOf(key) < 50) {
                    return false;
                }
                return true;
            }
        });
        //将源流数据进行处理，对每个数据拆分为多个数据（key不变），生成一个新的流
        /*KStream<String, String> stringStringKStream = textLines.flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")));
        //按key分组
        KGroupedStream<String, String> stringStringKGroupedStream = stringStringKStream.groupBy((key, word) -> word);

        KTable<String, Long> count = stringStringKGroupedStream.count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("counts-store"));
        KStream<String, Long> stringLongKStream = count.toStream();
        stringLongKStream.to("WordsWithCountsTopic", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), config);*/
        filter.to("my-filtered",Produced.with(Serdes.String(),Serdes.String()));
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), config);
        kafkaStreams.start();
    }
}
