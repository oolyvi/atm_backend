package com.bank.atm_monolithic.atm;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/atm/terminal")
public class AtmTerminalController {

    private final AtmTerminalService atmTerminalService;

    public AtmTerminalController(AtmTerminalService atmTerminalService) {
        this.atmTerminalService = atmTerminalService;
    }

    @PostMapping("/add")
    public ResponseEntity<AtmTerminal> addTerminal(@RequestBody AtmTerminal terminal) {
         /*
         http://localhost:8081/atm/terminal/add
        {
           "terminalCode": "ATM-KICK-01",
           "location": "Khaldoun 25",
           "status": "ACTIVE",
           "ipAddress": "127.0.0.1",
           "softwareVersion": "v2.1.1",
           "cassette1Count": 0,
           "cassette5Count": 0,
           "cassette10Count": 0,
           "cassette20Count": 0,
           "cassette50Count": 2,
           "cassette100Count": 0,
           "cassette200Count": 0,
           "cassette500Count": 0
        }
         */
        AtmTerminal savedTerminal = atmTerminalService.addTerminal(terminal);

        System.out.println("[ATM_DEBUG]: terminal was added for id: " + savedTerminal.getId());

        return ResponseEntity.ok(savedTerminal);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AtmTerminal>> getAllTerminals() {
        /*
         http://localhost:8081/atm/terminal/all
         */
        List<AtmTerminal> terminals = atmTerminalService.getAllTerminals();

        System.out.println("[ATM_DEBUG]: get all terminals");

        return ResponseEntity.ok(terminals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtmTerminal> getTerminalById(@PathVariable Long id) {
        /*
         http://localhost:8081/atm/terminal/1
         */

        System.out.println("[ATM_DEBUG]: get terminal for id: " + id);

        return atmTerminalService.getTerminalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}