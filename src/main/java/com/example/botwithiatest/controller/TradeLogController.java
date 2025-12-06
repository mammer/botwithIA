package com.example.botwithiatest.controller;

import com.example.botwithiatest.model.TradeLogEntry;
import com.example.botwithiatest.repository.TradeLogRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class TradeLogController {

    private final TradeLogRepository tradeLogRepository;

    public TradeLogController(TradeLogRepository tradeLogRepository) {
        this.tradeLogRepository = tradeLogRepository;
    }

    @GetMapping
    public List<TradeLogEntry> logs() {
        return tradeLogRepository.findAll();
    }
}
