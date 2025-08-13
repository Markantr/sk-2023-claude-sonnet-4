package com.happyheal.ui;

import com.happyheal.model.User;
import com.happyheal.database.DatabaseManager;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Main JavaFX application class.
 * Handles the login flow and main application window.
 */
public class MainApplication extends Application {
    
    private Stage primaryStage;
    private User currentUser;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Set application icon and title
        primaryStage.setTitle("HappyHeal - Product Workflow Management");
        
        try {
            // Load HappyHeal logo
            FileInputStream logoStream = new FileInputStream("icons/happyheal.svg");
            // Note: SVG support would need additional libraries, using as placeholder
            // For now, we'll create the UI without the image
        } catch (Exception e) {
            System.out.println("Could not load logo: " + e.getMessage());
        }
        
        // Show login screen
        showLoginScreen();
        
        primaryStage.show();
    }
    
    private void showLoginScreen() {
        VBox loginContainer = new VBox(20);
        loginContainer.setAlignment(Pos.CENTER);
        loginContainer.setPadding(new Insets(50));
        loginContainer.setStyle("-fx-background-color: #f0f8ff;");
        
        // Title
        Label titleLabel = new Label("HappyHeal");
        titleLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label subtitleLabel = new Label("Product Workflow Management");
        subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        
        // Login form
        GridPane loginForm = new GridPane();
        loginForm.setAlignment(Pos.CENTER);
        loginForm.setHgap(10);
        loginForm.setVgap(15);
        loginForm.setPadding(new Insets(20));
        loginForm.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-weight: bold;");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(200);
        
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(200);
        
        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2c5aa0; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        loginButton.setPrefWidth(100);
        
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");
        
        loginForm.add(usernameLabel, 0, 0);
        loginForm.add(usernameField, 1, 0);
        loginForm.add(passwordLabel, 0, 1);
        loginForm.add(passwordField, 1, 1);
        loginForm.add(loginButton, 0, 2, 2, 1);
        loginForm.add(messageLabel, 0, 3, 2, 1);
        
        // Set GridPane column constraints
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        loginForm.getColumnConstraints().addAll(col1, col2);
        
        GridPane.setHalignment(loginButton, javafx.geometry.HPos.CENTER);
        GridPane.setHalignment(messageLabel, javafx.geometry.HPos.CENTER);
        
        loginContainer.getChildren().addAll(titleLabel, subtitleLabel, loginForm);
        
        // Login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Please enter both username and password.");
                return;
            }
            
            User user = authenticateUser(username, password);
            if (user != null) {
                currentUser = user;
                showMainApplication();
            } else {
                messageLabel.setText("Invalid username or password.");
                passwordField.clear();
            }
        });
        
        // Allow Enter key to trigger login
        passwordField.setOnAction(e -> loginButton.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());
        
        Scene loginScene = new Scene(new BorderPane(loginContainer), 800, 600);
        primaryStage.setScene(loginScene);
        
        // Set focus to username field
        usernameField.requestFocus();
    }
    
    private User authenticateUser(String username, String password) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT username, password, display_name FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (password.equals(storedPassword)) {
                    User user = new User();
                    user.setUsername(rs.getString("username"));
                    user.setPassword(storedPassword);
                    user.setDisplayName(rs.getString("display_name"));
                    return user;
                }
            }
        } catch (Exception e) {
            System.err.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    private void showMainApplication() {
        MainController mainController = new MainController(currentUser);
        BorderPane mainLayout = mainController.createMainLayout();
        
        Scene mainScene = new Scene(mainLayout, 1200, 800);
        
        // Add CSS styling
        mainScene.getStylesheets().add(getClass().getResource("/styles.css") != null ? 
            getClass().getResource("/styles.css").toExternalForm() : "");
        
        primaryStage.setScene(mainScene);
        primaryStage.setMaximized(true);
        
        System.out.println("Welcome, " + currentUser.getDisplayName() + "!");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}