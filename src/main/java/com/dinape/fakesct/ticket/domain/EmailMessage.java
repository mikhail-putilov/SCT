package com.dinape.fakesct.ticket.domain;

import lombok.Builder;

import java.util.UUID;

@Builder(toBuilder = true)
public record EmailMessage(
        String subject,
        String body,
        String sender,
        String receiver,
        UUID ticketId
) {
}
