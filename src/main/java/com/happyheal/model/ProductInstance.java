package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an individual instance of a product with serial number and customer information.
 */
public class ProductInstance {
    
    @JsonProperty("SerialNumber")
    private String serialNumber;
    
    @JsonProperty("CustomerMail")
    private String customerMail;
    
    // Database fields
    private Long id;
    private String productId;
    private String currentStateId;
    private java.time.LocalDateTime purchaseDate;
    private String customerName;
    
    // Default constructor for Jackson
    public ProductInstance() {}
    
    public ProductInstance(String serialNumber, String customerMail) {
        this.serialNumber = serialNumber;
        this.customerMail = customerMail;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getSerialNumber() {
        return serialNumber;
    }
    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public String getCustomerMail() {
        return customerMail;
    }
    
    public void setCustomerMail(String customerMail) {
        this.customerMail = customerMail;
    }
    
    public String getCurrentStateId() {
        return currentStateId;
    }
    
    public void setCurrentStateId(String currentStateId) {
        this.currentStateId = currentStateId;
    }
    
    public java.time.LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }
    
    public void setPurchaseDate(java.time.LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    @Override
    public String toString() {
        return "ProductInstance{" +
                "serialNumber='" + serialNumber + '\'' +
                ", customerMail='" + customerMail + '\'' +
                ", currentStateId='" + currentStateId + '\'' +
                '}';
    }
}