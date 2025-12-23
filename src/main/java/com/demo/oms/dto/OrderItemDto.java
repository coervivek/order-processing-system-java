package com.demo.oms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class OrderItemDto {
    @NotBlank(message = "Product name is required")
    private String productName;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
    
    @Positive(message = "Price must be positive")
    private double price;

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
