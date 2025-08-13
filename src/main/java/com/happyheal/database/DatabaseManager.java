package com.happyheal.database;

import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Manages SQLite database connections and schema initialization.
 */
public class DatabaseManager {
    
    private static final String DATABASE_URL = "jdbc:sqlite:happyheal.db";
    
    /**
     * Creates a connection to the SQLite database.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }
    
    /**
     * Initializes the database by creating all necessary tables.
     */
    public static void initializeDatabase() {
        try {
            // Ensure database file directory exists
            Files.createDirectories(Paths.get("happyheal.db").getParent() != null ? 
                Paths.get("happyheal.db").getParent() : Paths.get("."));
            
            try (Connection conn = getConnection()) {
                createTables(conn);
                System.out.println("Database initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Enable foreign keys
        stmt.execute("PRAGMA foreign_keys = ON");
        
        // Create Users table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                username TEXT PRIMARY KEY,
                password TEXT NOT NULL,
                display_name TEXT NOT NULL
            )
        """);
        
        // Create Workflows table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS workflows (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL
            )
        """);
        
        // Create Workflow States table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS workflow_states (
                id TEXT PRIMARY KEY,
                workflow_id TEXT NOT NULL,
                title TEXT NOT NULL,
                color TEXT NOT NULL,
                FOREIGN KEY (workflow_id) REFERENCES workflows (id)
            )
        """);
        
        // Create Workflow Transitions table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS workflow_transitions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                workflow_id TEXT NOT NULL,
                type TEXT NOT NULL,
                from_state TEXT NOT NULL,
                to_state TEXT NOT NULL,
                setup_json TEXT,
                FOREIGN KEY (workflow_id) REFERENCES workflows (id),
                FOREIGN KEY (from_state) REFERENCES workflow_states (id),
                FOREIGN KEY (to_state) REFERENCES workflow_states (id)
            )
        """);
        
        // Create Product Groups table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS product_groups (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                workflow_id TEXT,
                parent_id TEXT,
                FOREIGN KEY (workflow_id) REFERENCES workflows (id),
                FOREIGN KEY (parent_id) REFERENCES product_groups (id)
            )
        """);
        
        // Create Products table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS products (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                product_number TEXT NOT NULL,
                group_id TEXT NOT NULL,
                FOREIGN KEY (group_id) REFERENCES product_groups (id)
            )
        """);
        
        // Create Product Instances table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS product_instances (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                product_id TEXT NOT NULL,
                serial_number TEXT NOT NULL UNIQUE,
                customer_mail TEXT,
                customer_name TEXT,
                current_state_id TEXT,
                purchase_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (product_id) REFERENCES products (id),
                FOREIGN KEY (current_state_id) REFERENCES workflow_states (id)
            )
        """);
        
        // Create indexes for better performance
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_product_instances_serial ON product_instances(serial_number)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_product_instances_customer ON product_instances(customer_mail)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_product_instances_state ON product_instances(current_state_id)");
        stmt.execute("CREATE INDEX IF NOT EXISTS idx_product_groups_parent ON product_groups(parent_id)");
        
        stmt.close();
    }
    
    /**
     * Checks if the database is properly initialized.
     */
    public static boolean isDatabaseInitialized() {
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "users", null);
            return tables.next();
        } catch (SQLException e) {
            return false;
        }
    }
    
    /**
     * Clears all data from the database (for testing purposes).
     */
    public static void clearDatabase() {
        try (Connection conn = getConnection()) {
            Statement stmt = conn.createStatement();
            
            // Disable foreign keys temporarily
            stmt.execute("PRAGMA foreign_keys = OFF");
            
            // Delete all data
            stmt.execute("DELETE FROM product_instances");
            stmt.execute("DELETE FROM products");
            stmt.execute("DELETE FROM product_groups");
            stmt.execute("DELETE FROM workflow_transitions");
            stmt.execute("DELETE FROM workflow_states");
            stmt.execute("DELETE FROM workflows");
            stmt.execute("DELETE FROM users");
            
            // Re-enable foreign keys
            stmt.execute("PRAGMA foreign_keys = ON");
            
            stmt.close();
            System.out.println("Database cleared successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to clear database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}