
package com.dinape.fakesct;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration(proxyBeanMethods = false)
public class KafkaConfig {

    @Bean
    public NewTopic emailTopic() {
        return TopicBuilder.name("email-topic").partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic ticketUpdateTopic() {
        return TopicBuilder.name("ticket-updates").partitions(1).replicas(1).build();
    }
}
