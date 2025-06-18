package com.dinape.fakesct.ticket.application.port.out;


import com.dinape.fakesct.ticket.domain.Ticket;
import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface GetTicketPort extends Repository<Ticket, Long> {
    Optional<Ticket> findByTicketDisplayId(String ticketDisplayId);
}
