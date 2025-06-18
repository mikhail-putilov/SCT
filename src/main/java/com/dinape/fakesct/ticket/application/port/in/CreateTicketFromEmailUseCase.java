package com.dinape.fakesct.ticket.application.port.in;

import com.dinape.fakesct.ticket.domain.EmailMessage;

public interface CreateTicketFromEmailUseCase {

    void createTicketFromEmail(EmailMessage emailMessage);
}
