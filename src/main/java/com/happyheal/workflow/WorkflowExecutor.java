package com.happyheal.workflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happyheal.database.DatabaseManager;
import com.happyheal.model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

/**
 * Executes workflow transitions and their associated actions.
 * Handles HTTP calls, file operations, email sending, and message boxes.
 */
public class WorkflowExecutor {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    
    /**
     * Executes a workflow transition for a product instance.
     * 
     * @param instanceId The ID of the product instance
     * @param transitionId The ID of the workflow transition
     * @return true if the transition was executed successfully
     */
    public static boolean executeTransition(Long instanceId, Long transitionId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            // Get product instance details
            ProductInstanceInfo instanceInfo = getProductInstanceInfo(conn, instanceId);
            if (instanceInfo == null) {
                System.err.println("Product instance not found: " + instanceId);
                return false;
            }
            
            // Get transition details
            WorkflowTransition transition = getTransitionDetails(conn, transitionId);
            if (transition == null) {
                System.err.println("Workflow transition not found: " + transitionId);
                return false;
            }
            
            // Create context for template processing
            Map<String, Object> context = MustacheTemplateEngine.createProductInstanceContext(
                instanceInfo.productName,
                instanceInfo.serialNumber,
                instanceInfo.customerMail,
                instanceInfo.customerName,
                instanceInfo.productNumber,
                instanceInfo.purchaseDate != null ? instanceInfo.purchaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null
            );
            
            // Execute the transition action
            boolean success = executeTransitionAction(transition, context);
            
            if (success) {
                // Update product instance state
                updateProductInstanceState(conn, instanceId, transition.getTo());
                System.out.println("Transition executed successfully: " + transition.getType());
                return true;
            } else {
                System.err.println("Failed to execute transition: " + transition.getType());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error executing workflow transition: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static ProductInstanceInfo getProductInstanceInfo(Connection conn, Long instanceId) throws Exception {
        String sql = """
            SELECT pi.serial_number, pi.customer_mail, pi.customer_name, pi.purchase_date,
                   p.name as product_name, p.product_number
            FROM product_instances pi
            JOIN products p ON pi.product_id = p.id
            WHERE pi.id = ?
        """;
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, instanceId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            ProductInstanceInfo info = new ProductInstanceInfo();
            info.serialNumber = rs.getString("serial_number");
            info.customerMail = rs.getString("customer_mail");
            info.customerName = rs.getString("customer_name");
            info.productName = rs.getString("product_name");
            info.productNumber = rs.getString("product_number");
            
            String purchaseDateStr = rs.getString("purchase_date");
            if (purchaseDateStr != null) {
                info.purchaseDate = LocalDateTime.parse(purchaseDateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
            
            return info;
        }
        
        return null;
    }
    
    private static WorkflowTransition getTransitionDetails(Connection conn, Long transitionId) throws Exception {
        String sql = "SELECT type, from_state, to_state, setup_json FROM workflow_transitions WHERE id = ?";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, transitionId);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            WorkflowTransition transition = new WorkflowTransition();
            transition.setId(transitionId);
            transition.setType(rs.getString("type"));
            transition.setFrom(rs.getString("from_state"));
            transition.setTo(rs.getString("to_state"));
            
            String setupJson = rs.getString("setup_json");
            if (setupJson != null) {
                Map<String, Object> setup = objectMapper.readValue(setupJson, Map.class);
                transition.setSetup(setup);
            }
            
            return transition;
        }
        
        return null;
    }
    
    private static boolean executeTransitionAction(WorkflowTransition transition, Map<String, Object> context) {
        switch (transition.getType().toLowerCase()) {
            case "http":
                return executeHttpAction(transition, context);
            case "file":
                return executeFileAction(transition, context);
            case "mail":
                return executeMailAction(transition, context);
            case "messagebox":
                return executeMessageBoxAction(transition, context);
            case "noaction":
                return true; // No action required
            default:
                System.err.println("Unknown transition type: " + transition.getType());
                return false;
        }
    }
    
    private static boolean executeHttpAction(WorkflowTransition transition, Map<String, Object> context) {
        try {
            String endpoint = transition.getEndpoint();
            String body = transition.getBody();
            
            if (endpoint == null) {
                System.err.println("HTTP endpoint not specified");
                return false;
            }
            
            // Process templates
            endpoint = MustacheTemplateEngine.processTemplate(endpoint, context);
            if (body != null) {
                body = MustacheTemplateEngine.processTemplate(body, context);
            }
            
            // Build HTTP request
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json");
            
            if (body != null) {
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(body));
            } else {
                requestBuilder.GET();
            }
            
            HttpRequest request = requestBuilder.build();
            
            // Send request
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("HTTP request successful: " + response.statusCode());
                return true;
            } else {
                System.err.println("HTTP request failed: " + response.statusCode() + " - " + response.body());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("Error executing HTTP action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean executeFileAction(WorkflowTransition transition, Map<String, Object> context) {
        try {
            String filePath = transition.getPath();
            String content = transition.getContent();
            Boolean append = transition.getAppend();
            
            if (filePath == null || content == null) {
                System.err.println("File path or content not specified");
                return false;
            }
            
            // Process templates
            filePath = MustacheTemplateEngine.processTemplate(filePath, context);
            content = MustacheTemplateEngine.processTemplate(content, context);
            
            Path path = Paths.get(filePath);
            
            // Create directories if they don't exist
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            
            // Write to file
            if (Boolean.TRUE.equals(append)) {
                Files.writeString(path, content + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } else {
                Files.writeString(path, content);
            }
            
            System.out.println("File action completed: " + filePath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing file action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean executeMailAction(WorkflowTransition transition, Map<String, Object> context) {
        try {
            String from = transition.getFromEmail();
            String to = transition.getToEmail();
            String subject = transition.getSubject();
            String body = transition.getBody();
            
            if (from == null || to == null || subject == null) {
                System.err.println("Email parameters not fully specified");
                return false;
            }
            
            // Process templates
            from = MustacheTemplateEngine.processTemplate(from, context);
            to = MustacheTemplateEngine.processTemplate(to, context);
            subject = MustacheTemplateEngine.processTemplate(subject, context);
            if (body != null) {
                body = MustacheTemplateEngine.processTemplate(body, context);
            }
            
            // For demonstration purposes, we'll just log the email action
            // In a real implementation, you would configure SMTP settings
            System.out.println("EMAIL ACTION:");
            System.out.println("From: " + from);
            System.out.println("To: " + to);
            System.out.println("Subject: " + subject);
            System.out.println("Body: " + (body != null ? body : "(no body)"));
            System.out.println("--- EMAIL END ---");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing mail action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean executeMessageBoxAction(WorkflowTransition transition, Map<String, Object> context) {
        try {
            String content = transition.getContent();
            
            if (content == null) {
                System.err.println("Message content not specified");
                return false;
            }
            
            // Process template
            content = MustacheTemplateEngine.processTemplate(content, context);
            
            // Show JavaFX alert dialog
            javafx.application.Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Workflow Notification");
                alert.setHeaderText("Product Status Update");
                alert.setContentText(content);
                alert.showAndWait();
            });
            
            System.out.println("Message box displayed: " + content);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error executing message box action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static void updateProductInstanceState(Connection conn, Long instanceId, String newStateId) throws Exception {
        String sql = "UPDATE product_instances SET current_state_id = ? WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newStateId);
        pstmt.setLong(2, instanceId);
        pstmt.executeUpdate();
        pstmt.close();
    }
    
    // Helper class for product instance information
    private static class ProductInstanceInfo {
        String serialNumber;
        String customerMail;
        String customerName;
        String productName;
        String productNumber;
        LocalDateTime purchaseDate;
    }
}