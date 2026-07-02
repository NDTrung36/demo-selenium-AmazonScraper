package com.trung.amazonscraper.controller;

import com.trung.amazonscraper.model.dto.ScrapeRequestDTO;
import com.trung.amazonscraper.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {

    private final ScraperService scraperService;

    @Autowired
    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/amazon")
    public ResponseEntity<String> startAmazonScraping(@RequestBody ScrapeRequestDTO request) {
        if (request.getTargetUrl() == null || request.getTargetUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng cung cấp targetUrl hợp lệ.");
        }

        try {
            String savedFilePath = scraperService.processScrapingAndExport(request.getTargetUrl());
            return ResponseEntity.ok("Quá trình cào dữ liệu hoàn tất. File được lưu tại: " + savedFilePath);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi trong quá trình xử lý: " + e.getMessage());
        }
    }
}