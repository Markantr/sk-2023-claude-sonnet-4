package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a state in a workflow with its display properties.
 */
public class WorkflowState {
    
    @JsonProperty("Id")
    private String id;
    
    @JsonProperty("Title")
    private String title;
    
    @JsonProperty("Color")
    private String color;
    
    // Database fields
    private String workflowId;
    
    // Default constructor for Jackson
    public WorkflowState() {}
    
    public WorkflowState(String id, String title, String color) {
        this.id = id;
        this.title = title;
        this.color = color;
    }
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getWorkflowId() {
        return workflowId;
    }
    
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
    
    @Override
    public String toString() {
        return "WorkflowState{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}