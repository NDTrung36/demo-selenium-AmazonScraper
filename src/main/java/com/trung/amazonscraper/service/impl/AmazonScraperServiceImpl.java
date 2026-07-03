package com.trung.amazonscraper.service.impl;

import com.trung.amazonscraper.model.Product;
import com.trung.amazonscraper.service.ScraperService;
import com.trung.amazonscraper.util.CsvExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmazonScraperServiceImpl implements ScraperService {

    private static final Logger log = LogManager.getLogger(AmazonScraperServiceImpl.class);

    private final CsvExporter csvExporter;

    @Autowired
    public AmazonScraperServiceImpl(CsvExporter csvExporter) {
        this.csvExporter = csvExporter;
    }

    @Override
    public String processScrapingAndExport(String targetUrl) {
        log.info("Bắt đầu cào dữ liệu từ URL: {}", targetUrl);

        List<Product> products = scrapeData(targetUrl);
        if (products.isEmpty()) {
            log.warn("Không tìm thấy dữ liệu hoặc URL không hợp lệ: {}", targetUrl);
            return "Không tìm thấy dữ liệu hoặc URL không hợp lệ.";
        }

        String fileName = "Amazon_Result_" + System.currentTimeMillis() + ".csv";
        String exportedFile = csvExporter.exportProducts(products, fileName);
        log.info("Đã xuất {} sản phẩm ra file {}", products.size(), exportedFile);
        return exportedFile;
    }

    private List<Product> scrapeData(String targetUrl) {
        List<Product> products = new ArrayList<>();
        List<String> asinList = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            log.info("Đang mở trang tìm kiếm...");
            driver.get(targetUrl);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-component-type='s-search-result']")));
            List<WebElement> items = driver.findElements(By.cssSelector("div[data-component-type='s-search-result']"));

            log.info("Tìm thấy {} khối sản phẩm trên trang tìm kiếm.", items.size());

            for (int i = 0; i < Math.min(7, items.size()); i++) {
                String asin = items.get(i).getAttribute("data-asin");
                if (asin != null && !asin.isEmpty()) {
                    asinList.add(asin);
                }
            }

            for (String asin : asinList) {
                String detailUrl = "https://www.amazon.co.jp/dp/" + asin;
                log.debug("Đang mở trang chi tiết sản phẩm: {}", detailUrl);
                driver.get(detailUrl);

                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));

                    String title = driver.findElement(By.id("productTitle")).getText().trim();
                    String imgUrl = driver.findElement(By.id("landingImage")).getAttribute("src");

                    String price = "Không có giá / Hết hàng";
                    try {
                        WebElement priceElement = driver.findElement(By.cssSelector("span.a-price-whole"));
                        price = priceElement.getText() + " Yên";
                    } catch (Exception ignored) {
                    }

                    products.add(new Product(asin, title, price, imgUrl));

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Bị gián đoạn khi chờ giữa các sản phẩm", e);
                        break;
                    }
                } catch (Exception e) {
                    log.warn("Lỗi khi cào ASIN {} (có thể dính CAPTCHA)", asin, e);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi hệ thống trong quá trình cào", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return products;
    }
}
