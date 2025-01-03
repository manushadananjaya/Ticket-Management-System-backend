package com.Yasindu.ticketingsystem.ticketingsystem.model;

import com.Yasindu.ticketingsystem.ticketingsystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vendor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Vendor.class);
    private final TicketPool ticketPool;
    private final int ticketsToRelease;
    private final int maxCapacity;
    private final LogService logService; // Inject LogService

    public Vendor(TicketPool ticketPool, int ticketsToRelease, int maxCapacity, LogService logService) {
        this.ticketPool = ticketPool;
        this.ticketsToRelease = ticketsToRelease;
        this.maxCapacity = maxCapacity;
        this.logService = logService;
    }

    @Override
    public void run() {
        for (int i = 0; i < ticketsToRelease; i++) {
            synchronized (ticketPool) {
                if (ticketPool.getAvailableTickets() >= maxCapacity) {
                    String logMessage = Thread.currentThread().getName() + ": Max ticket capacity reached. Pausing ticket release.";
                    logger.info(logMessage);
                    logService.addLog(logMessage);
                    break;
                }
                String ticket = "Ticket-" + System.nanoTime();
                ticketPool.addTicket(ticket);
                String logMessage = Thread.currentThread().getName() + ": Added " + ticket;
                logger.info(logMessage);
                logService.addLog(logMessage);
            }

            try {
                Thread.sleep(100); // Simulate delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String logMessage = Thread.currentThread().getName() + ": Vendor interrupted";
                logger.error(logMessage, e);
                logService.addLog(logMessage);
            }
        }
    }
}
