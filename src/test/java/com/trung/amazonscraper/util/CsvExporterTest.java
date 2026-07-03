package com.trung.amazonscraper.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvExporterTest {

    private final CsvExporter csvExporter = new CsvExporter();

    @Test
    void formatTitleForCsv_shouldCollapseWhitespace() {
        String result = csvExporter.formatTitleForCsv("  Apple   iPhone  17e   256GB   ");

        assertEquals("Apple iPhone 17e 256GB", result);
    }

    @Test
    void formatTitleForCsv_shouldShortenLongTitleAtWordBoundary() {
        String result = csvExporter.formatTitleForCsv(
                "Apple iPhone 17e 256GB: 6.1\" Super Retina XDR Display, A19 Chip, All-Day Battery, 48MP Fusion Camera, Minimum Storage 256GB; Black + 2 Year Extension AppleCare+ Theft and Lost Plan"
        );

        assertTrue(result.endsWith("..."));
        assertTrue(result.length() <= 83);
        assertTrue(result.startsWith("Apple iPhone 17e 256GB"));
    }
}
