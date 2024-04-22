package com.example.db;
import java.util.Objects;

public class Product {
    private String category;
    private int quantity;
    private String productID;
    private String productName;
    private int price;

    public Product(String category, int quantity, String productID, String productName, int price) {
        this.category = category;
        this.quantity = quantity;
        this.productID = productID;
        this.productName = productName;
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "Product{" +
                "category='" + category + '\'' +
                ", quantity=" + quantity +
                ", productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                '}';
    }
}

