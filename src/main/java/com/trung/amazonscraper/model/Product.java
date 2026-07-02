package com.trung.amazonscraper.model;

public class Product {
    private String asin;
    private String title;
    private String price;
    private String imageUrl;

    public Product(String asin, String title, String price, String imageUrl) {
        this.asin = asin;
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getAsin() { return asin; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
