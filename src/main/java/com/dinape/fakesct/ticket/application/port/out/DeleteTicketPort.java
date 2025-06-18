package com.dinape.fakesct.ticket.application.port.out;


import com.dinape.fakesct.ticket.domain.Ticket;
import org.springframework.data.repository.Repository;

public interface DeleteTicketPort extends Repository<Ticket, Long> {
    void deleteAll();
}
