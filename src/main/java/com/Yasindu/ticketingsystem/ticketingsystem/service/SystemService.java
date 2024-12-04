package com.Yasindu.ticketingsystem.ticketingsystem.service;

import com.Yasindu.ticketingsystem.ticketingsystem.model.SystemConfig;
import com.Yasindu.ticketingsystem.ticketingsystem.model.TicketPool;
import com.Yasindu.ticketingsystem.ticketingsystem.repository.SystemConfigRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SystemService {

    private final SystemConfigRepository configRepository;
    private final ThreadService threadService;

    public SystemService(SystemConfigRepository configRepository, ThreadService threadService) {
        this.configRepository = configRepository;
        this.threadService = threadService;
    }

    /**
     * Save the system configuration to the database.
     *
     * @param config System configuration to save
     * @return The saved SystemConfig
     */
    public SystemConfig saveConfig(SystemConfig config) {
        // Clear existing configuration to allow only one active configuration
        clearConfig();
        return configRepository.save(config);
    }

    /**
     * Retrieve the active system configuration.
     *
     * @return Optional containing the active configuration, if available
     */
    public Optional<SystemConfig> getConfig() {
        return configRepository.findAll().stream().findFirst();
    }

    /**
     * Delete all existing configurations from the database.
     */
    public void clearConfig() {
        configRepository.deleteAll();
    }

    /**
     * Start the ticketing system with the active configuration.
     *
     * @param config Optional containing the active configuration
     * @param ticketPool The ticket pool to be used by threads
     */
    public void startSystem(Optional<SystemConfig> config, TicketPool ticketPool) {
        if (config.isPresent()) {
            threadService.configure(config.get(), ticketPool);
            threadService.startThreads();
        } else {
            throw new IllegalStateException("No active configuration found. Please configure the system first.");
        }
    }

    /**
     * Stop the ticketing system and all active threads.
     */
    public void stopSystem() {
        threadService.stopThreads();
    }
}
