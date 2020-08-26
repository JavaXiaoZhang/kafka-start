package com.zq;

import com.zq.provider.KafkaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class KafkaStartApplication {
    /*@Autowired
    private KafkaProvider provider;

    @PostConstruct
    public void init(){
        for(int i=0;i<10;i++){
            //provider
        }
    }*/
    public static void main(String[] args) {
        SpringApplication.run(KafkaStartApplication.class);
    }
}
