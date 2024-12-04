package com.Yasindu.ticketingsystem.ticketingsystem.model;

import com.Yasindu.ticketingsystem.ticketingsystem.service.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Customer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Customer.class);
    private final TicketPool ticketPool;
    private final int retrievalLimit;
    private final LogService logService; // Inject LogService

    public Customer(TicketPool ticketPool, int retrievalLimit, LogService logService) {
        this.ticketPool = ticketPool;
        this.retrievalLimit = retrievalLimit;
        this.logService = logService;
    }

    @Override
    public void run() {
        for (int i = 0; i < retrievalLimit; i++) {
            try {
                synchronized (ticketPool) {
                    if (ticketPool.getAvailableTickets() == 0) {
                        String logMessage = Thread.currentThread().getName() + ": No tickets available. Waiting...";
                        logger.info(logMessage);
                        logService.addLog(logMessage);
                    }
                    String ticket = ticketPool.removeTicket();
                    String logMessage = Thread.currentThread().getName() + ": Purchased " + ticket;
                    logger.info(logMessage);
                    logService.addLog(logMessage);
                }
                Thread.sleep(200); // Simulate delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                String logMessage = Thread.currentThread().getName() + ": Customer interrupted";
                logger.error(logMessage, e);
                logService.addLog(logMessage);
                break;
            }
        }
        String logMessage = Thread.currentThread().getName() + ": Finished purchasing tickets.";
        logger.info(logMessage);
        logService.addLog(logMessage);
    }
}
