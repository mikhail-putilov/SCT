package com.dinape.fakesct.ticket.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dinape.fakesct.ticket.application.port.out.SendMessageToKafkaPort;
import com.dinape.fakesct.ticket.domain.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAdapter implements SendMessageToKafkaPort {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @SneakyThrows
    public void sendEnrichedEmailMessage(EmailMessage message) {
        String ticketJson = objectMapper.writeValueAsString(message);
        kafkaTemplate.send("ticket-updates", ticketJson);
    }
}
