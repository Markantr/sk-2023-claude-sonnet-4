package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a product group that can contain subgroups and/or products.
 * This forms a hierarchical tree structure.
 */
public class ProductGroup {
    
    @JsonProperty("Id")
    private String id;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("Workflow")
    private String workflow;
    
    @JsonProperty("SubGroups")
    private List<ProductGroup> subGroups = new ArrayList<>();
    
    @JsonProperty("Products")
    private List<Product> products = new ArrayList<>();
    
    // Database fields
    private String parentId;
    
    // Default constructor for Jackson
    public ProductGroup() {}
    
    public ProductGroup(String id, String name, String workflow) {
        this.id = id;
        this.name = name;
        this.workflow = workflow;
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
    
    public String getWorkflow() {
        return workflow;
    }
    
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    
    public List<ProductGroup> getSubGroups() {
        return subGroups;
    }
    
    public void setSubGroups(List<ProductGroup> subGroups) {
        this.subGroups = subGroups != null ? subGroups : new ArrayList<>();
    }
    
    public List<Product> getProducts() {
        return products;
    }
    
    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }
    
    public String getParentId() {
        return parentId;
    }
    
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
    
    /**
     * Recursively flattens the product group tree to get all products.
     */
    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>(products);
        for (ProductGroup subGroup : subGroups) {
            allProducts.addAll(subGroup.getAllProducts());
        }
        return allProducts;
    }
    
    /**
     * Recursively flattens the product group tree to get all groups.
     */
    public List<ProductGroup> getAllGroups() {
        List<ProductGroup> allGroups = new ArrayList<>();
        allGroups.add(this);
        for (ProductGroup subGroup : subGroups) {
            allGroups.addAll(subGroup.getAllGroups());
        }
        return allGroups;
    }
    
    @Override
    public String toString() {
        return "ProductGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", workflow='" + workflow + '\'' +
                ", subGroupCount=" + (subGroups != null ? subGroups.size() : 0) +
                ", productCount=" + (products != null ? products.size() : 0) +
                '}';
    }
}