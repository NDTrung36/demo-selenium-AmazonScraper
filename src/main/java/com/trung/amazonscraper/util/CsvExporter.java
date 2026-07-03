package com.trung.amazonscraper.util;

import com.trung.amazonscraper.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class CsvExporter {

    private static final Logger log = LogManager.getLogger(CsvExporter.class);
    private static final int MAX_TITLE_LENGTH = 80;

    public String exportProducts(List<Product> products, String fileName) {
        String filePath = "exports/" + fileName;

        try {
            Files.createDirectories(Path.of("exports"));

            try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
                writer.write('\ufeff');
                writer.println("ASIN,Title,Price,ImageURL");

                for (Product p : products) {
                    String safeTitle = formatTitleForCsv(p.getTitle()).replace("\"", "\"\"");
                    writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", p.getAsin(), safeTitle, p.getPrice(), p.getImageUrl()));
                }
            }

            log.info("Đã ghi file CSV tại {}", filePath);
            return filePath;
        } catch (Exception e) {
            log.error("Lỗi khi xuất file CSV {}", filePath, e);
            throw new RuntimeException("Lỗi khi xuất file CSV", e);
        }
    }

    String formatTitleForCsv(String title) {
        if (title == null || title.isBlank()) {
            return "";
        }

        String normalized = title.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= MAX_TITLE_LENGTH) {
            return normalized;
        }

        int cut = normalized.lastIndexOf(' ', MAX_TITLE_LENGTH - 1);
        if (cut < MAX_TITLE_LENGTH / 2) {
            cut = MAX_TITLE_LENGTH;
        }

        return normalized.substring(0, cut).trim() + "...";
    }
}
