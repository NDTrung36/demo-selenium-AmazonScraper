package com.trung.amazonscraper.service.impl;

import com.trung.amazonscraper.model.Product;
import com.trung.amazonscraper.service.ScraperService;
import com.trung.amazonscraper.util.CsvExporter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AmazonScraperServiceImpl implements ScraperService {

    private final CsvExporter csvExporter;

    @Autowired
    public AmazonScraperServiceImpl(CsvExporter csvExporter) {
        this.csvExporter = csvExporter;
    }

    @Override
    public String processScrapingAndExport(String targetUrl) {
        System.out.println("Bắt đầu cào dữ liệu từ URL: " + targetUrl);
        List<Product> products = scrapeData(targetUrl);

        if (products.isEmpty()) {
            return "Không tìm thấy dữ liệu hoặc URL không hợp lệ.";
        }

        String fileName = "Amazon_Result_" + System.currentTimeMillis() + ".csv";
        return csvExporter.exportProducts(products, fileName);
    }

    private List<Product> scrapeData(String targetUrl) {
        List<Product> products = new ArrayList<>();
        List<String> asinList = new ArrayList<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            System.out.println("Đang mở trang tìm kiếm...");
            driver.get(targetUrl);

            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[data-component-type='s-search-result']")));
            List<WebElement> items = driver.findElements(By.cssSelector("div[data-component-type='s-search-result']"));

            System.out.println("Tìm thấy " + items.size() + " khối sản phẩm trên trang tìm kiếm.");

            for (int i = 0; i < Math.min(7, items.size()); i++) {
                String asin = items.get(i).getAttribute("data-asin");
                if (asin != null && !asin.isEmpty()) {
                    asinList.add(asin);
                }
            }

            for (String asin : asinList) {
                String detailUrl = "https://www.amazon.co.jp/dp/" + asin;
                System.out.println("\nLink sản phẩm " + detailUrl);
                driver.get(detailUrl);

                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("productTitle")));

                    String title = driver.findElement(By.id("productTitle")).getText().trim();
                    String imgUrl = driver.findElement(By.id("landingImage")).getAttribute("src");

                    String price = "Không có giá / Hết hàng";
                    try {
                        WebElement priceElement = driver.findElement(By.cssSelector("span.a-price-whole"));
                        price = priceElement.getText() + " Yên";
                    } catch (Exception e) {
                    }

                    products.add(new Product(asin, title, price, imgUrl));

                    Thread.sleep(2000);

                } catch (Exception e) {
                    System.out.println("Lỗi khi cào ASIN: " + asin + " (Có thể dính CAPTCHA)");
                }
            }

        } catch (Exception e) {
            System.out.println("Lỗi hệ thống trong quá trình cào: " + e.getMessage());
        } finally {
            driver.quit();
        }

        return products;
    }
}