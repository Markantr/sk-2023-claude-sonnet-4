package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * Root data model that represents the complete application data structure.
 */
public class AppData {
    
    @JsonProperty("ProductGroupTree")
    private List<ProductGroup> productGroupTree = new ArrayList<>();
    
    @JsonProperty("Workflows")
    private List<Workflow> workflows = new ArrayList<>();
    
    @JsonProperty("Users")
    private List<User> users = new ArrayList<>();
    
    // Default constructor for Jackson
    public AppData() {}
    
    public AppData(List<ProductGroup> productGroupTree, List<Workflow> workflows, List<User> users) {
        this.productGroupTree = productGroupTree != null ? productGroupTree : new ArrayList<>();
        this.workflows = workflows != null ? workflows : new ArrayList<>();
        this.users = users != null ? users : new ArrayList<>();
    }
    
    // Getters and setters
    public List<ProductGroup> getProductGroupTree() {
        return productGroupTree;
    }
    
    public void setProductGroupTree(List<ProductGroup> productGroupTree) {
        this.productGroupTree = productGroupTree != null ? productGroupTree : new ArrayList<>();
    }
    
    public List<Workflow> getWorkflows() {
        return workflows;
    }
    
    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows != null ? workflows : new ArrayList<>();
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public void setUsers(List<User> users) {
        this.users = users != null ? users : new ArrayList<>();
    }
    
    /**
     * Gets a workflow by its ID.
     */
    public Workflow getWorkflow(String workflowId) {
        return workflows.stream()
                .filter(workflow -> workflowId.equals(workflow.getId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets a user by username.
     */
    public User getUser(String username) {
        return users.stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all products from all product groups.
     */
    public List<Product> getAllProducts() {
        List<Product> allProducts = new ArrayList<>();
        for (ProductGroup group : productGroupTree) {
            allProducts.addAll(group.getAllProducts());
        }
        return allProducts;
    }
    
    /**
     * Gets all product groups from the tree structure.
     */
    public List<ProductGroup> getAllProductGroups() {
        List<ProductGroup> allGroups = new ArrayList<>();
        for (ProductGroup group : productGroupTree) {
            allGroups.addAll(group.getAllGroups());
        }
        return allGroups;
    }
    
    /**
     * Gets all product instances from all products.
     */
    public List<ProductInstance> getAllProductInstances() {
        List<ProductInstance> allInstances = new ArrayList<>();
        for (Product product : getAllProducts()) {
            allInstances.addAll(product.getInstances());
        }
        return allInstances;
    }
    
    @Override
    public String toString() {
        return "AppData{" +
                "productGroupCount=" + (productGroupTree != null ? productGroupTree.size() : 0) +
                ", workflowCount=" + (workflows != null ? workflows.size() : 0) +
                ", userCount=" + (users != null ? users.size() : 0) +
                '}';
    }
}