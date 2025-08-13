package com.happyheal.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.happyheal.model.*;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * Imports initial data from JSON file into the SQLite database.
 */
public class DataImporter {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Imports all initial data from data.json if the database is empty.
     */
    public static void importInitialData() {
        try {
            if (isDatabaseEmpty()) {
                System.out.println("Database is empty. Importing initial data...");
                
                // Load data from JSON file
                AppData appData = loadDataFromJson();
                
                // Import data into database
                importUsers(appData.getUsers());
                importWorkflows(appData.getWorkflows());
                importProductGroups(appData.getProductGroupTree());
                
                System.out.println("Initial data import completed successfully.");
            } else {
                System.out.println("Database already contains data. Skipping initial import.");
            }
        } catch (Exception e) {
            System.err.println("Failed to import initial data: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Data import failed", e);
        }
    }
    
    private static boolean isDatabaseEmpty() throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            return rs.getInt(1) == 0;
        }
    }
    
    private static AppData loadDataFromJson() throws IOException {
        File jsonFile = new File("data.json");
        return objectMapper.readValue(jsonFile, AppData.class);
    }
    
    private static void importUsers(List<User> users) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO users (username, password, display_name) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            for (User user : users) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getPassword());
                pstmt.setString(3, user.getDisplayName());
                pstmt.executeUpdate();
            }
            
            pstmt.close();
            System.out.println("Imported " + users.size() + " users.");
        }
    }
    
    private static void importWorkflows(List<Workflow> workflows) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            importWorkflowEntities(conn, workflows);
            importWorkflowStates(conn, workflows);
            importWorkflowTransitions(conn, workflows);
            
            System.out.println("Imported " + workflows.size() + " workflows.");
        }
    }
    
    private static void importWorkflowEntities(Connection conn, List<Workflow> workflows) throws SQLException {
        String sql = "INSERT INTO workflows (id, name) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Workflow workflow : workflows) {
            pstmt.setString(1, workflow.getId());
            pstmt.setString(2, workflow.getName());
            pstmt.executeUpdate();
        }
        
        pstmt.close();
    }
    
    private static void importWorkflowStates(Connection conn, List<Workflow> workflows) throws SQLException {
        String sql = "INSERT INTO workflow_states (id, workflow_id, title, color) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Workflow workflow : workflows) {
            for (WorkflowState state : workflow.getStates()) {
                pstmt.setString(1, state.getId());
                pstmt.setString(2, workflow.getId());
                pstmt.setString(3, state.getTitle());
                pstmt.setString(4, state.getColor());
                pstmt.executeUpdate();
            }
        }
        
        pstmt.close();
    }
    
    private static void importWorkflowTransitions(Connection conn, List<Workflow> workflows) throws SQLException {
        String sql = "INSERT INTO workflow_transitions (workflow_id, type, from_state, to_state, setup_json) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Workflow workflow : workflows) {
            for (WorkflowTransition transition : workflow.getTransitions()) {
                pstmt.setString(1, workflow.getId());
                pstmt.setString(2, transition.getType());
                pstmt.setString(3, transition.getFrom());
                pstmt.setString(4, transition.getTo());
                
                // Convert setup map to JSON string
                String setupJson = null;
                if (transition.getSetup() != null) {
                    try {
                        setupJson = objectMapper.writeValueAsString(transition.getSetup());
                    } catch (Exception e) {
                        System.err.println("Failed to serialize setup for transition: " + e.getMessage());
                    }
                }
                pstmt.setString(5, setupJson);
                
                pstmt.executeUpdate();
            }
        }
        
        pstmt.close();
    }
    
    private static void importProductGroups(List<ProductGroup> rootGroups) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Import groups in tree order (parent before children)
            for (ProductGroup rootGroup : rootGroups) {
                importProductGroupTree(conn, rootGroup, null);
            }
            
            // Import products for all groups
            for (ProductGroup rootGroup : rootGroups) {
                importProductsForGroupTree(conn, rootGroup);
            }
            
            System.out.println("Imported product group tree with products.");
        }
    }
    
    private static void importProductGroupTree(Connection conn, ProductGroup group, String parentId) throws SQLException {
        // Import current group
        String sql = "INSERT INTO product_groups (id, name, workflow_id, parent_id) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, group.getId());
        pstmt.setString(2, group.getName());
        pstmt.setString(3, group.getWorkflow());
        pstmt.setString(4, parentId);
        pstmt.executeUpdate();
        pstmt.close();
        
        // Import subgroups recursively
        for (ProductGroup subGroup : group.getSubGroups()) {
            importProductGroupTree(conn, subGroup, group.getId());
        }
    }
    
    private static void importProductsForGroupTree(Connection conn, ProductGroup group) throws SQLException {
        // Import products for current group
        importProducts(conn, group.getProducts(), group.getId());
        
        // Import products for subgroups recursively
        for (ProductGroup subGroup : group.getSubGroups()) {
            importProductsForGroupTree(conn, subGroup);
        }
    }
    
    private static void importProducts(Connection conn, List<Product> products, String groupId) throws SQLException {
        String productSql = "INSERT INTO products (id, name, product_number, group_id) VALUES (?, ?, ?, ?)";
        PreparedStatement productPstmt = conn.prepareStatement(productSql);
        
        String instanceSql = "INSERT INTO product_instances (product_id, serial_number, customer_mail, current_state_id) VALUES (?, ?, ?, ?)";
        PreparedStatement instancePstmt = conn.prepareStatement(instanceSql);
        
        for (Product product : products) {
            // Import product
            productPstmt.setString(1, product.getId());
            productPstmt.setString(2, product.getName());
            productPstmt.setString(3, product.getProductNumber());
            productPstmt.setString(4, groupId);
            productPstmt.executeUpdate();
            
            // Import product instances
            for (ProductInstance instance : product.getInstances()) {
                instancePstmt.setString(1, product.getId());
                instancePstmt.setString(2, instance.getSerialNumber());
                instancePstmt.setString(3, instance.getCustomerMail());
                
                // Set initial state (first state of the product group's workflow)
                String initialStateId = getInitialStateForGroup(conn, groupId);
                instancePstmt.setString(4, initialStateId);
                
                instancePstmt.executeUpdate();
            }
        }
        
        productPstmt.close();
        instancePstmt.close();
    }
    
    private static String getInitialStateForGroup(Connection conn, String groupId) throws SQLException {
        String sql = """
            SELECT ws.id 
            FROM workflow_states ws
            JOIN workflows w ON ws.workflow_id = w.id
            JOIN product_groups pg ON pg.workflow_id = w.id
            WHERE pg.id = ?
            ORDER BY ws.rowid LIMIT 1
        """;
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, groupId);
        ResultSet rs = pstmt.executeQuery();
        
        String stateId = null;
        if (rs.next()) {
            stateId = rs.getString(1);
        }
        
        rs.close();
        pstmt.close();
        
        return stateId;
    }
}