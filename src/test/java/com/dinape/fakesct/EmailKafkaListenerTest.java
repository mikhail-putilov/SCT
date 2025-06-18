package com.dinape.fakesct;

import com.dinape.fakesct.ticket.application.port.out.DeleteTicketPort;
import com.dinape.fakesct.ticket.application.port.out.GetTicketPort;
import com.dinape.fakesct.ticket.domain.EmailMessage;
import com.dinape.fakesct.ticket.domain.Ticket;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"email-topic", "ticket-updates"})
public class EmailKafkaListenerTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private DeleteTicketPort deleteTicketPort;

    @Autowired
    private GetTicketPort getTicketPort;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void cleanup() {
        deleteTicketPort.deleteAll();
    }

    @Test
    void happyPath_createsTicketFromKafkaMessage() throws Exception {
        // Arrange
        EmailMessage message = EmailMessage.builder()
                .subject("New issue reported - ID-1234")
                .body("This is the email body")
                .sender("john@example.com")
                .receiver("support@example.com")
                .build();

        String jsonMessage = objectMapper.writeValueAsString(message);

        // Act
        kafkaTemplate.send("email-topic", jsonMessage);

        // Assert DB has the new ticket
        await().until(() -> getTicketPort.findByTicketDisplayId("1234").isPresent());

        Ticket ticket = getTicketPort.findByTicketDisplayId("1234").get();
        assertThat(ticket.status()).isEqualTo("created");

        // Optional: verify a message was published to "ticket-updates"
        // Set up a consumer manually
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put("auto.offset.reset", "earliest");

        DefaultKafkaConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                new StringDeserializer()
        );

        Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "ticket-updates");

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, "ticket-updates", Duration.ofMinutes(1));
        assertThat(record).isNotNull();
        assertThat(record.value()).contains("1234");
    }
}
