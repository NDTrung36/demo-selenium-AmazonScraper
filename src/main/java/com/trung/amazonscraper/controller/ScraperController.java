package com.trung.amazonscraper.controller;

import com.trung.amazonscraper.model.dto.ScrapeRequestDTO;
import com.trung.amazonscraper.service.ScraperService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scraper")
public class ScraperController {

    private static final Logger log = LogManager.getLogger(ScraperController.class);

    private final ScraperService scraperService;

    @Autowired
    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }

    @PostMapping("/amazon")
    public ResponseEntity<String> startAmazonScraping(@RequestBody ScrapeRequestDTO request) {
        if (request.getTargetUrl() == null || request.getTargetUrl().isEmpty()) {
            log.warn("Request /api/scraper/amazon thiếu targetUrl");
            return ResponseEntity.badRequest().body("Vui lòng cung cấp targetUrl hợp lệ.");
        }

        try {
            log.info("Nhận yêu cầu cào Amazon cho URL: {}", request.getTargetUrl());
            String savedFilePath = scraperService.processScrapingAndExport(request.getTargetUrl());
            log.info("Hoàn tất cào dữ liệu, file kết quả: {}", savedFilePath);
            return ResponseEntity.ok("Quá trình cào dữ liệu hoàn tất. File được lưu tại: " + savedFilePath);

        } catch (Exception e) {
            log.error("Lỗi trong quá trình xử lý yêu cầu cào Amazon", e);
            return ResponseEntity.internalServerError().body("Lỗi trong quá trình xử lý: " + e.getMessage());
        }
    }
}
