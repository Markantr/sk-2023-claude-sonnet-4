package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents a transition between workflow states with its configuration.
 */
public class WorkflowTransition {
    
    @JsonProperty("Type")
    private String type;
    
    @JsonProperty("From")
    private String from;
    
    @JsonProperty("To")
    private String to;
    
    @JsonProperty("Setup")
    private Map<String, Object> setup;
    
    // Database fields
    private Long id;
    private String workflowId;
    
    // Default constructor for Jackson
    public WorkflowTransition() {}
    
    public WorkflowTransition(String type, String from, String to, Map<String, Object> setup) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.setup = setup;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public Map<String, Object> getSetup() {
        return setup;
    }
    
    public void setSetup(Map<String, Object> setup) {
        this.setup = setup;
    }
    
    public String getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    
    // Helper methods for specific transition types
    public String getEndpoint() {
        return setup != null ? (String) setup.get("Endpoint") : null;
    }
    
    public String getBody() {
        return setup != null ? (String) setup.get("Body") : null;
    }
    
    public String getContent() {
        return setup != null ? (String) setup.get("Content") : null;
    }
    
    public String getPath() {
        return setup != null ? (String) setup.get("Path") : null;
    }
    
    public Boolean getAppend() {
        return setup != null ? (Boolean) setup.get("Append") : null;
    }
    
    public String getFromEmail() {
        return setup != null ? (String) setup.get("From") : null;
    }
    
    public String getToEmail() {
        return setup != null ? (String) setup.get("To") : null;
    }
    
    public String getSubject() {
        return setup != null ? (String) setup.get("Subject") : null;
    }
    
    @Override
    public String toString() {
        return "WorkflowTransition{" +
                "type='" + type + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}