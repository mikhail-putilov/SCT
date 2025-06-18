package com.dinape.fakesct.ticket.domain;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import java.util.UUID;


@Builder(toBuilder = true)
public record Ticket(
        @Id UUID id,
        String ticketDisplayId,
        String status,
        @Version Long version
) {
}
