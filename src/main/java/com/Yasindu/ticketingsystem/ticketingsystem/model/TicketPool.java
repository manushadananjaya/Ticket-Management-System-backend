package com.Yasindu.ticketingsystem.ticketingsystem.model;

import com.Yasindu.ticketingsystem.ticketingsystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class TicketPool {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);
    private final Queue<String> tickets = new LinkedList<>();
    private final LogService logService; // Inject LogService

    public TicketPool(LogService logService) {
        this.logService = logService;
    }

    public synchronized void addTicket(String ticket) {
        tickets.add(ticket);
        String logMessage = "Added ticket: " + ticket;
        logger.info(logMessage);
        logService.addLog(logMessage);
        notifyAll();
    }

    public synchronized String removeTicket() throws InterruptedException {
        while (tickets.isEmpty()) {
            wait();
        }
        String ticket = tickets.poll();
        String logMessage = "Removed ticket: " + ticket;
        logger.info(logMessage);
        logService.addLog(logMessage);
        return ticket;
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }
}
