package com.lrn.containerize.config;

import com.lrn.containerize.exception.ServiceException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableKafka
@Configuration
public class KafkaConfig {
    private Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Autowired
    DiscoveryClient discoveryClient;

    @Value("${restapp.kafka_service}")
    String kafkaServiceName;

    @Value("${restapp.kafka_groupid}")
    String kafkaConsumerGroupId;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaBrokers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG,kafkaConsumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "10");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "15000");
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    public String getKafkaBrokers() {
        logger.debug("Kafka service name : {}", kafkaServiceName);
        final List<ServiceInstance> serviceInstanceList = discoveryClient.getInstances(kafkaServiceName);
        if(serviceInstanceList!=null && !serviceInstanceList.isEmpty()) {
             String kafkaBrokers = serviceInstanceList.stream()
                    .map(serviceInstance -> String.format(serviceInstance.getHost() + ":" + serviceInstance.getPort()))
                    .collect(Collectors.joining(","));
            logger.debug("Found Kafka service '{}', broker list : {}", kafkaServiceName, kafkaBrokers);
            return kafkaBrokers;
        } else {
            throw new ServiceException(String .format("Failed to find service %s in the consul.", kafkaServiceName));
        }
    }

}
