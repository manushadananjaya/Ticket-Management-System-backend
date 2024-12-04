package com.Yasindu.ticketingsystem.ticketingsystem.service;

import com.Yasindu.ticketingsystem.ticketingsystem.model.Customer;
import com.Yasindu.ticketingsystem.ticketingsystem.model.SystemConfig;
import com.Yasindu.ticketingsystem.ticketingsystem.model.TicketPool;
import com.Yasindu.ticketingsystem.ticketingsystem.model.Vendor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    private final LogService logService; // Inject LogService
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = false;

    public ThreadService(LogService logService) {
        this.logService = logService;
    }

    /**
     * Configure the system by creating vendor and customer threads based on the configuration.
     *
     * @param config The active system configuration
     * @param ticketPool The shared ticket pool for the threads
     */
    public void configure(SystemConfig config, TicketPool ticketPool) {
        threads.clear();

        int ticketsPerVendor = config.getTicketReleaseRate();
        int retrievalRatePerCustomer = config.getCustomerRetrievalRate();

        // Create vendor threads
        for (int i = 0; i < config.getNumberOfVendors(); i++) {
            threads.add(new Thread(
                    new Vendor(ticketPool, ticketsPerVendor, config.getMaxTicketCapacity(), logService),
                    "Vendor-" + (i + 1)
            ));
        }

        // Create customer threads
        for (int i = 0; i < config.getNumberOfCustomers(); i++) {
            threads.add(new Thread(
                    new Customer(ticketPool, retrievalRatePerCustomer, logService),
                    "Customer-" + (i + 1)
            ));
        }

        logService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");
    }

    /**
     * Start all configured threads for vendors and customers.
     */
    public void startThreads() {
        if (running) {
            throw new IllegalStateException("Threads are already running.");
        }
        running = true;

        for (Thread thread : threads) {
            thread.start();
        }

        logService.addLog("All threads started.");
    }

    /**
     * Stop all running threads gracefully.
     */
    public void stopThreads() {
        if (!running) {
            throw new IllegalStateException("Threads are not running.");
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }
        running = false;

        logService.addLog("All threads stopped.");
    }
}
