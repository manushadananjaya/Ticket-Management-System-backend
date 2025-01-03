package com.Yasindu.ticketingsystem.ticketingsystem.controller;

import com.Yasindu.ticketingsystem.ticketingsystem.model.SystemConfig;
import com.Yasindu.ticketingsystem.ticketingsystem.model.TicketPool;
import com.Yasindu.ticketingsystem.ticketingsystem.service.LogService;
import com.Yasindu.ticketingsystem.ticketingsystem.service.SystemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class SystemController {
    private final SystemService systemService;
    private final TicketPool ticketPool;
    private final LogService logService; // Inject LogService

    public SystemController(SystemService systemService, TicketPool ticketPool, LogService logService) {
        this.systemService = systemService;
        this.ticketPool = ticketPool;
        this.logService = logService;
    }

    @PostMapping("/config")
    public ResponseEntity<Map<String, Object>> configureSystem(@RequestBody @Valid SystemConfig config) {
        systemService.clearConfig();
        SystemConfig savedConfig = systemService.saveConfig(config);

        // Construct the response to include ticketsAvailable (totalTickets)
        Map<String, Object> response = new HashMap<>();
        response.put("ticketsAvailable", savedConfig.getTotalTickets());

        // Log the configuration action
        logService.addLog("System configured with " + config.getNumberOfVendors() + " vendors and " + config.getNumberOfCustomers() + " customers.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/config")
    public ResponseEntity<SystemConfig> getConfig() {
        return systemService.getConfig()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSystem() {
        Optional<SystemConfig> config = systemService.getConfig();
        if (config.isEmpty()) {
            return ResponseEntity.badRequest().body("Configuration not found. Please configure the system first.");
        }
        systemService.startSystem(config, ticketPool);

        // Log the start action
        logService.addLog("System started successfully.");

        return ResponseEntity.ok("System started successfully!");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        systemService.stopSystem();

        // Log the stop action
        logService.addLog("System stopped successfully.");

        return ResponseEntity.ok("System stopped successfully!");
    }

    // New API endpoint to fetch logs
    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = logService.getLogs();
        return ResponseEntity.ok(logs);
    }

    @DeleteMapping("/logs")
    public ResponseEntity<String> clearLogs() {
        logService.clearLogs();
        return ResponseEntity.ok("Logs cleared successfully!");
    }
}
