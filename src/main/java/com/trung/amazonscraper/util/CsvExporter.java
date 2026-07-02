package com.trung.amazonscraper.util;

import com.trung.amazonscraper.model.Product;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvExporter {

    public String exportProducts(List<Product> products, String fileName) {
        String filePath = "exports/" + fileName;

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write('\ufeff');
            writer.println("ASIN,Title,Price,ImageURL");

            for (Product p : products) {
                String safeTitle = p.getTitle().replace("\"", "\"\"");
                writer.println(String.format("\"%s\",\"%s\",\"%s\",\"%s\"", p.getAsin(), safeTitle, p.getPrice(), p.getImageUrl()));
            }
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xuất file CSV: " + e.getMessage());
        }
    }
}
