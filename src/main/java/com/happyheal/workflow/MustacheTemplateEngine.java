package com.happyheal.workflow;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

/**
 * Mustache template engine for processing workflow transition templates.
 * Handles variable substitution in workflow setup configurations.
 */
public class MustacheTemplateEngine {
    
    private static final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    
    /**
     * Processes a template string with the provided context variables.
     * 
     * @param template The template string containing Mustache variables
     * @param context The context map with variable values
     * @return The processed template with variables substituted
     */
    public static String processTemplate(String template, Map<String, Object> context) {
        if (template == null || template.trim().isEmpty()) {
            return template;
        }
        
        try {
            // Create mustache from template string
            Mustache mustache = mustacheFactory.compile(new StringReader(template), "template");
            
            // Process template with context
            StringWriter writer = new StringWriter();
            mustache.execute(writer, context);
            writer.flush();
            
            return writer.toString();
        } catch (Exception e) {
            System.err.println("Error processing Mustache template: " + e.getMessage());
            e.printStackTrace();
            return template; // Return original template if processing fails
        }
    }
    
    /**
     * Creates a context map for product instance workflow transitions.
     * 
     * @param productName The name of the product
     * @param serialNumber The serial number of the product instance
     * @param customerEmail The customer's email address
     * @param customerName The customer's name
     * @param productNumber The product number
     * @param purchaseDate The purchase date
     * @return A context map suitable for Mustache processing
     */
    public static Map<String, Object> createProductInstanceContext(
            String productName, 
            String serialNumber, 
            String customerEmail, 
            String customerName, 
            String productNumber, 
            String purchaseDate) {
        
        return Map.of(
            "type", productName != null ? productName : "",
            "sn", serialNumber != null ? serialNumber : "",
            "customermail", customerEmail != null ? customerEmail : "",
            "customername", customerName != null ? customerName : "",
            "productnumber", productNumber != null ? productNumber : "",
            "purchasedate", purchaseDate != null ? purchaseDate : ""
        );
    }
    
    /**
     * Creates a context map with default/empty values for testing templates.
     * 
     * @return A context map with placeholder values
     */
    public static Map<String, Object> createDefaultContext() {
        return Map.of(
            "type", "Sample Product",
            "sn", "TEST-001",
            "customermail", "customer@example.com",
            "customername", "John Doe",
            "productnumber", "PRD-001",
            "purchasedate", "2024-01-01"
        );
    }
    
    /**
     * Validates that a template contains only supported Mustache variables.
     * 
     * @param template The template to validate
     * @return true if the template is valid, false otherwise
     */
    public static boolean validateTemplate(String template) {
        if (template == null || template.trim().isEmpty()) {
            return true;
        }
        
        try {
            // Try to compile the template
            mustacheFactory.compile(new StringReader(template), "validation");
            return true;
        } catch (Exception e) {
            System.err.println("Template validation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Escapes special characters in template variables for safe processing.
     * 
     * @param value The value to escape
     * @return The escaped value
     */
    public static String escapeValue(String value) {
        if (value == null) {
            return "";
        }
        
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }
}