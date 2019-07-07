package com.lrn.dataload.kafka;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class DataLoaderKafka {
    private Logger logger = LoggerFactory.getLogger(DataLoaderKafka.class);
    private String topicName = "contacts_update";
    private String fileName = "contacts_detail_update_data.txt";

    public static void main(String[] args) {
        System.out.println("Kafka integration is running ..................................................");
        DataLoaderKafka dataLoaderKafka = new DataLoaderKafka();
        dataLoaderKafka.loadData();
        System.out.println("FINISHED : Kafka integration ..................................................");
    }

    public void loadData() {
        String consul_agent = System.getenv("consul_service");
        String consult_agent_port = System.getenv("consul_service_port");
        String kafkaService = System.getenv("kafka_service");
        logger.debug("consul_service = " + consul_agent);
        logger.debug("consul_service_port = " + consult_agent_port);
        logger.debug("kafka_service = " + kafkaService);
        final Optional<List<Triple<String, String, Integer>>> kafkaServiceAddress = getServiceServerAndPortFromConsul(consul_agent, Integer.valueOf(consult_agent_port), kafkaService);
        if(!kafkaServiceAddress.isPresent()) {
           throw new ContextedRuntimeException("Failed to locate kafka service from consul.").addContextValue("consul_agent_host", consul_agent).addContextValue("port", consult_agent_port);
        }
        final List<Triple<String, String, Integer>> addressList = kafkaServiceAddress.get();
//      send message to kafka
        sendMessageToKafka(addressList);
    }


    public void sendMessageToKafka(List<Triple<String, String, Integer>> addressList) {
        Properties config = new Properties();
        config.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaIntegrationWithContactsServiceIT");
        final String bootStrapBrokers = addressList.stream().map(triple -> String.format("%s:%d", triple.getMiddle(), triple.getRight())).collect(Collectors.joining(","));
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapBrokers);
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        final KafkaProducer<Long, String> kvKafkaProducer = new KafkaProducer<>(config);

        final Callback callback = (recordMetadata, exp) -> {
            if (exp != null) {
                logger.error("Failed to send message to Kafka : " + exp.getLocalizedMessage(), exp);
            }
        };

        try(final InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
            if(resourceAsStream == null) {
                throw new IllegalStateException("Failed to read file "+ fileName);
            }
            final List<String> jsonData = IOUtils.readLines(resourceAsStream, StandardCharsets.UTF_8);
            jsonData.forEach(json -> {
                ProducerRecord<Long,String> producerRecord = new ProducerRecord<>(topicName,System.currentTimeMillis(),json);
                kvKafkaProducer.send(producerRecord, callback);
            });
            logger.info("Message sent to Kafka successfully.");
        } catch (IOException exp) {
            logger.error("Failed to read data file " + fileName + " : " + exp.getLocalizedMessage(), exp);
        } finally {
            kvKafkaProducer.flush();
            kvKafkaProducer.close();
        }

    }

    public Optional<List<Triple<String, String, Integer>>> getServiceServerAndPortFromConsul(String consulAgent, Integer port, String serviceName) {
        ConsulClient consulClient = new ConsulClient(consulAgent, port);
        HealthServicesRequest healthServicesRequest = HealthServicesRequest.newBuilder()
                .setPassing(true)
                .setQueryParams(QueryParams.DEFAULT)
                .build();
        Response<List<HealthService>> healthServices = consulClient.getHealthServices(serviceName, healthServicesRequest);
        if(healthServices.getValue() == null || healthServices.getValue().isEmpty()) {
            logger.info("service not found : {}", serviceName);
            return Optional.empty();
        }
        List<Triple<String, String, Integer>> serviceAddressDetailList = new ArrayList<>();
        for (HealthService healthService : healthServices.getValue()) {
            String ipAddress = healthService.getService().getAddress();
            Integer servicePort = healthService.getService().getPort();
            logger.debug("Service Name: {}, Address: {}:{}", serviceName, ipAddress, servicePort);
            Triple<String, String, Integer> serviceAddDetail = new ImmutableTriple<>(serviceName, ipAddress, servicePort);
            serviceAddressDetailList.add(serviceAddDetail);
        }
        logger.info("Found {} node for service {}.", healthServices.getValue().size(), serviceName);
        return Optional.of(serviceAddressDetailList);
    }
}
