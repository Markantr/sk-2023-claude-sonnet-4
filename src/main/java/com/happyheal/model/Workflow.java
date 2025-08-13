package com.happyheal.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a complete workflow with its states and transitions.
 */
public class Workflow {
    
    @JsonProperty("Id")
    private String id;
    
    @JsonProperty("Name")
    private String name;
    
    @JsonProperty("States")
    private List<WorkflowState> states = new ArrayList<>();
    
    @JsonProperty("Transitions")
    private List<WorkflowTransition> transitions = new ArrayList<>();
    
    // Default constructor for Jackson
    public Workflow() {}
    
    public Workflow(String id, String name) {
        this.id = id;
        this.name = name;
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
    
    public List<WorkflowState> getStates() {
        return states;
    }
    
    public void setStates(List<WorkflowState> states) {
        this.states = states != null ? states : new ArrayList<>();
    }
    
    public List<WorkflowTransition> getTransitions() {
        return transitions;
    }
    
    public void setTransitions(List<WorkflowTransition> transitions) {
        this.transitions = transitions != null ? transitions : new ArrayList<>();
    }
    
    /**
     * Gets a state by its ID.
     */
    public WorkflowState getState(String stateId) {
        return states.stream()
                .filter(state -> stateId.equals(state.getId()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all transitions from a specific state.
     */
    public List<WorkflowTransition> getTransitionsFrom(String stateId) {
        return transitions.stream()
                .filter(transition -> stateId.equals(transition.getFrom()))
                .toList();
    }
    
    /**
     * Gets the initial state (first state in the list).
     */
    public WorkflowState getInitialState() {
        return states.isEmpty() ? null : states.get(0);
    }
    
    @Override
    public String toString() {
        return "Workflow{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", stateCount=" + (states != null ? states.size() : 0) +
                ", transitionCount=" + (transitions != null ? transitions.size() : 0) +
                '}';
    }
}