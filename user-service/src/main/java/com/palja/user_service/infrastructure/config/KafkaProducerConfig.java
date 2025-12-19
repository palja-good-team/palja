package com.palja.user_service.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.palja.common.interceptor.KafkaProducerInterceptor;

import io.micrometer.tracing.Tracer;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

	private final String BOOTSTRAP_SERVER;

	public KafkaProducerConfig(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServer) {
		this.BOOTSTRAP_SERVER = bootstrapServer;
	}

	@Bean
	public KafkaProducerInterceptor<Object> kafkaEventProducerInterceptor(Tracer tracer) {
		return new KafkaProducerInterceptor<>(tracer);
	}

	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> config = new HashMap<>();
		config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
		config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		return new DefaultKafkaProducerFactory<>(config);
	}

	@Bean
	public KafkaTemplate<String, Object> userEventKafkaTemplate(KafkaProducerInterceptor<Object> kafkaProducerInterceptor) {
		KafkaTemplate<String, Object> kafkaTemplate = new KafkaTemplate<>(producerFactory());
		kafkaTemplate.setProducerInterceptor(kafkaProducerInterceptor);

		return kafkaTemplate;
	}

}
