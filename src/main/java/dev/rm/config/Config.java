package dev.rm.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.web.client.RestTemplate;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import dev.rm.model.AlertMessage;
import dev.rm.model.VitalSignsMessage;

@Configuration
public class Config {

    @Value("${KAFKA_BOOTSTRAP_SERVERS}")
    private String bootstrapServers;

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // ==> RabbitMQ configuration <==

    @Bean
    public Queue patientStatusQueue() {
        return new Queue("patientStatusQueue", true);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ==> VitalSignsConsumer Kafka configuration <==

    @Bean
    public ConsumerFactory<String, VitalSignsMessage> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "vitalSignsGroup");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<VitalSignsMessage> jsonDeserializer = new JsonDeserializer<>(VitalSignsMessage.class);
        jsonDeserializer.addTrustedPackages("*");

        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, VitalSignsMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // ==> Alerts Kafka configuration <==

    @Bean
    public ProducerFactory<String, AlertMessage> alertProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AlertMessage> alertKafkaTemplate() {
        return new KafkaTemplate<>(alertProducerFactory());
    }

    public ConsumerFactory<String, AlertMessage> alertConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, "alertsGroup");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        JsonDeserializer<AlertMessage> jsonDeserializer = new JsonDeserializer<>(AlertMessage.class);
        jsonDeserializer.addTrustedPackages("*");

        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer);
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), jsonDeserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<?> alertKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AlertMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(alertConsumerFactory());
        return factory;
    }

}
