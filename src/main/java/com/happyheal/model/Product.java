package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a product with its basic information and instances.
 */
public class Product {
    
    @JsonProperty("Id")
    private String id;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("ProductNumber")
    private String productNumber;
    
    @JsonProperty("Instances")
    private List<ProductInstance> instances = new ArrayList<>();
    
    // Database fields
    private String groupId;
    
    // Default constructor for Jackson
    public Product() {}
    
    public Product(String id, String name, String productNumber) {
        this.id = id;
        this.name = name;
        this.productNumber = productNumber;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getProductNumber() {
        return productNumber;
    }
    
    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }
    
    public List<ProductInstance> getInstances() {
        return instances;
    }
    
    public void setInstances(List<ProductInstance> instances) {
        this.instances = instances != null ? instances : new ArrayList<>();
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", productNumber='" + productNumber + '\'' +
                ", instanceCount=" + (instances != null ? instances.size() : 0) +
                '}';
    }
}