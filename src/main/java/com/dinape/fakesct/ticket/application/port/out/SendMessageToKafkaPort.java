package com.dinape.fakesct.ticket.application.port.out;

import com.dinape.fakesct.ticket.domain.EmailMessage;

public interface SendMessageToKafkaPort {

    void sendEnrichedEmailMessage(EmailMessage message);
}
