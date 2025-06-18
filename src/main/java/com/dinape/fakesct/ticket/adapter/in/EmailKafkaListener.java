package com.dinape.fakesct.ticket.adapter.in;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dinape.fakesct.ticket.application.port.in.CreateTicketFromEmailUseCase;
import com.dinape.fakesct.ticket.domain.EmailMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailKafkaListener {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CreateTicketFromEmailUseCase createTicketFromEmailUseCase;

    @KafkaListener(topics = "email-topic", groupId = "ticketing-group")
    public void handleEmailMessage(String messageJson) {
        try {
            EmailMessage email = objectMapper.readValue(messageJson, EmailMessage.class);

            createTicketFromEmailUseCase.createTicketFromEmail(email);
        } catch (RuntimeException | JsonProcessingException e) {
            log.error("Something bad happened: [{}]", e.getMessage(), e);
        }
    }

}
