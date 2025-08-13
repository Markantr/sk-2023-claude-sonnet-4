package com.happyheal;

import com.happyheal.database.DatabaseManager;
import com.happyheal.database.DataImporter;
import com.happyheal.ui.MainApplication;
import javafx.application.Application;

/**
 * Main entry point for the HappyHeal Product Workflow Management application.
 * This class initializes the database, imports initial data, and launches the JavaFX application.
 */
public class Main {
    
    public static void main(String[] args) {
        try {
            // Initialize database and create tables if they don't exist
            DatabaseManager.initializeDatabase();
            
            // Import initial data from JSON file
            DataImporter.importInitialData();
            
            // Launch JavaFX application
            Application.launch(MainApplication.class, args);
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}