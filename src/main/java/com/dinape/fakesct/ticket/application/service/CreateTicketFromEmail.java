package com.dinape.fakesct.ticket.application.service;

import com.dinape.fakesct.ticket.application.port.in.CreateTicketFromEmailUseCase;
import com.dinape.fakesct.ticket.application.port.out.GetTicketPort;
import com.dinape.fakesct.ticket.application.port.out.SaveTicketPort;
import com.dinape.fakesct.ticket.application.port.out.SendMessageToKafkaPort;
import com.dinape.fakesct.ticket.domain.EmailMessage;
import com.dinape.fakesct.ticket.domain.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateTicketFromEmail implements CreateTicketFromEmailUseCase {

    private final GetTicketPort getTicketPort;
    private final SaveTicketPort saveTicketPort;
    private final SendMessageToKafkaPort sendMessageToKafkaPort;

    private final Pattern ID_PATTERN = Pattern.compile("ID-(\\w+)");

    public void createTicketFromEmail(EmailMessage emailMessage) {

        String ticketDisplayId = extractTicketDisplayIdFromSubject(emailMessage.subject());

        Optional<Ticket> existingTicket = ticketDisplayId == null ? Optional.empty() : getTicketPort.findByTicketDisplayId(ticketDisplayId);

        Ticket.TicketBuilder builder;
        if (existingTicket.isEmpty()) {
            builder = Ticket.builder();
            builder.id(UUID.randomUUID());
            builder.ticketDisplayId(ticketDisplayId);
            builder.status("created");
            var ticket = builder.build();
            saveTicketPort.save(builder.build());
            emailMessage = emailMessage.toBuilder().ticketId(ticket.id()).build();
        } else {
            emailMessage = emailMessage.toBuilder().ticketId(existingTicket.get().id()).build();
        }

        sendMessageToKafkaPort.sendEnrichedEmailMessage(emailMessage);
        // Send Kafka message
    }

    private String extractTicketDisplayIdFromSubject(String subject) {
        Matcher matcher = ID_PATTERN.matcher(subject);
        return matcher.find() ? matcher.group(1) : null;
    }
}
