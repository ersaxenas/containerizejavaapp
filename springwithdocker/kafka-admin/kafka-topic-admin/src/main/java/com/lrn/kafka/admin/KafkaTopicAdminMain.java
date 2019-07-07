package com.lrn.kafka.admin;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.health.HealthServicesRequest;
import com.ecwid.consul.v1.health.model.HealthService;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KafkaTopicAdminMain {
    private Logger logger = LoggerFactory.getLogger(KafkaTopicAdminMain.class);
    private List<String> topicNames = Arrays.asList("contacts_update");

    public void createKafkaTopics() {
        String consul_agent = System.getenv("consul_service");
        String consult_agent_port = System.getenv("consul_service_port");
        String kafkaService = System.getenv("kafka_service");
        logger.debug("consul_service = " + consul_agent);
        logger.debug("consul_service_port = " + consult_agent_port);
        logger.debug("kafka_service = " + kafkaService);
        Optional<List<Triple<String, String, Integer>>> kafkaServiceAddList = getServiceServerAndPortFromConsul(consul_agent, Integer.valueOf(consult_agent_port), kafkaService);
        if (kafkaServiceAddList.isPresent()) {
            String kafkaBrokers = kafkaServiceAddList.get().stream().map(serviceAddress -> serviceAddress.getMiddle() + ":" + serviceAddress.getRight()).collect(Collectors.joining(","));
            logger.debug("Kafka brokers config :" + kafkaBrokers);
            Properties kafkaClientConfig = new Properties();
            kafkaClientConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
            AdminClient adminClient = AdminClient.create(kafkaClientConfig);
            topicNames.forEach(topicName -> {
                CreateTopicsResult createTopicsResult = adminClient.createTopics(Arrays.asList(new NewTopic(topicName, 3, (short) 1)));
                try {
                    createTopicsResult.values().get(topicName).get();
                } catch (InterruptedException | ExecutionException exp) {
                    if (exp.getCause().getClass().isAssignableFrom(TopicExistsException.class)) {
                        logger.debug("Topic already exists : " + topicName);
                    } else {
                        logger.error("Failed to create topic" + exp.getLocalizedMessage(), exp);
                        throw new RuntimeException("Failed to create topic " + exp.getLocalizedMessage(), exp);
                    }
                }
                logger.info("Topic {} successfully created.", topicName);
            });
            adminClient.close();
        } else {
            logger.info("Kakfa is not available. System cannot proceed ahead.  required service" + kafkaService);
        }
    }

    public static void main(String[] args) {
        KafkaTopicAdminMain kafkaTopicAdminMain = new KafkaTopicAdminMain();
        kafkaTopicAdminMain.createKafkaTopics();
    }


    private void checkServiceFromLocal() {
        /*method is just for testing on host node where docker deamon is reachable*/
        logger.info("starting consul client");
        ConsulClient consulClient = new ConsulClient("172.24.0.2", 8500);
        HealthServicesRequest healthServicesRequest = HealthServicesRequest.newBuilder()
                .setPassing(true)
                .setQueryParams(QueryParams.DEFAULT)
                .build();
        Response<List<HealthService>> healthServices = consulClient.getHealthServices("zookeeper-server", healthServicesRequest);
        healthServices.getValue().stream().forEach(healthService -> {
            String address = healthService.getService().getAddress();
            Integer port = healthService.getService().getPort();
            logger.info(address + ":" + port);

        });
        logger.info("finished consul client");
    }

    public Optional<List<Triple<String, String, Integer>>> getServiceServerAndPortFromConsul(String consulAgent, Integer port, String serviceName) {
        ConsulClient consulClient = new ConsulClient
                (consulAgent, port);
        HealthServicesRequest healthServicesRequest = HealthServicesRequest.newBuilder()
                .setPassing(true)
                .setQueryParams(QueryParams.DEFAULT)
                .build();
        Response<List<HealthService>> healthServices = consulClient.getHealthServices(serviceName, healthServicesRequest);
        if (healthServices.getValue() == null || healthServices.getValue().isEmpty()) {
            logger.info("service not found : {}", serviceName);
            return Optional.empty();
        }
        List<Triple<String, String, Integer>> serviceAddressDetailList = new ArrayList<>();
        for (HealthService healthService : healthServices.getValue()) {
            String ipAddress = healthService.getService().getAddress();
            Integer servicePort = healthService.getService().getPort();
            logger.debug("Service Name: {}, Address: {}:{}", serviceName, ipAddress, port);
            Triple<String, String, Integer> serviceAddDetail = new ImmutableTriple<>(serviceName, ipAddress, servicePort);
            serviceAddressDetailList.add(serviceAddDetail);
        }
        logger.info("Found {} node for service {}.", healthServices.getValue().size(), serviceName);
        return Optional.of(serviceAddressDetailList);
    }

}
