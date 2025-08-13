package com.happyheal.ui;

import com.happyheal.model.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Main controller for the application's primary interface.
 * Manages navigation and content display.
 */
public class MainController {
    
    private final User currentUser;
    private BorderPane mainLayout;
    private TreeView<String> navigationTree;
    private VBox contentArea;
    
    public MainController(User currentUser) {
        this.currentUser = currentUser;
    }
    
    public BorderPane createMainLayout() {
        mainLayout = new BorderPane();
        
        // Create header
        HBox header = createHeader();
        mainLayout.setTop(header);
        
        // Create navigation tree
        VBox navigationPanel = createNavigationPanel();
        mainLayout.setLeft(navigationPanel);
        
        // Create main content area
        contentArea = createContentArea();
        mainLayout.setCenter(contentArea);
        
        // Create status bar
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);
        
        return mainLayout;
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(10, 15, 10, 15));
        header.setSpacing(10);
        header.setStyle("-fx-background-color: #2c5aa0; -fx-text-fill: white;");
        
        Label titleLabel = new Label("HappyHeal - Product Workflow Management");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Welcome, " + currentUser.getDisplayName());
        userLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #1a4480; -fx-text-fill: white; -fx-border-color: white; -fx-border-radius: 3;");
        logoutButton.setOnAction(e -> {
            // For now, just close the application
            System.exit(0);
        });
        
        header.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return header;
    }
    
    private VBox createNavigationPanel() {
        VBox navigationPanel = new VBox();
        navigationPanel.setPrefWidth(300);
        navigationPanel.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 0 1 0 0;");
        
        Label navLabel = new Label("Navigation");
        navLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15 15 10 15;");
        
        navigationTree = createNavigationTree();
        navigationTree.setPadding(new Insets(0, 10, 10, 10));
        
        VBox.setVgrow(navigationTree, Priority.ALWAYS);
        navigationPanel.getChildren().addAll(navLabel, navigationTree);
        
        return navigationPanel;
    }
    
    private TreeView<String> createNavigationTree() {
        TreeItem<String> rootItem = new TreeItem<>("HappyHeal");
        rootItem.setExpanded(true);
        
        // Product Groups
        TreeItem<String> productGroupsItem = new TreeItem<>("Product Groups");
        productGroupsItem.setExpanded(true);
        
        // Add main product categories from data
        TreeItem<String> dentalCareItem = new TreeItem<>("Dental Care and Oral Hygiene Products");
        TreeItem<String> cardiacItem = new TreeItem<>("Cardiac Monitoring and Heart Health Products");
        TreeItem<String> endoscopyItem = new TreeItem<>("Endoscopy and Surgical Instruments");
        
        productGroupsItem.getChildren().addAll(dentalCareItem, cardiacItem, endoscopyItem);
        
        // Workflows
        TreeItem<String> workflowsItem = new TreeItem<>("Workflows");
        TreeItem<String> defaultStockingItem = new TreeItem<>("Default Stocking");
        TreeItem<String> extendedStockingItem = new TreeItem<>("Extended Stocking");
        workflowsItem.getChildren().addAll(defaultStockingItem, extendedStockingItem);
        
        // Product Management
        TreeItem<String> productsItem = new TreeItem<>("Product Management");
        TreeItem<String> allProductsItem = new TreeItem<>("All Products");
        TreeItem<String> productInstancesItem = new TreeItem<>("Product Instances");
        productsItem.getChildren().addAll(allProductsItem, productInstancesItem);
        
        // Reports
        TreeItem<String> reportsItem = new TreeItem<>("Reports");
        TreeItem<String> workflowReportItem = new TreeItem<>("Workflow Status Report");
        TreeItem<String> customerReportItem = new TreeItem<>("Customer Report");
        reportsItem.getChildren().addAll(workflowReportItem, customerReportItem);
        
        rootItem.getChildren().addAll(productGroupsItem, workflowsItem, productsItem, reportsItem);
        
        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
        
        // Handle tree selection
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                handleNavigationSelection(newSelection.getValue());
            }
        });
        
        return treeView;
    }
    
    private VBox createContentArea() {
        VBox contentArea = new VBox();
        contentArea.setPadding(new Insets(20));
        contentArea.setSpacing(15);
        
        // Welcome content
        Label welcomeLabel = new Label("Welcome to HappyHeal Product Workflow Management");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label instructionLabel = new Label("Select an item from the navigation panel to get started.");
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        // Quick stats or dashboard content
        HBox statsBox = createQuickStats();
        
        contentArea.getChildren().addAll(welcomeLabel, instructionLabel, statsBox);
        return contentArea;
    }
    
    private HBox createQuickStats() {
        HBox statsBox = new HBox(20);
        statsBox.setPadding(new Insets(20, 0, 0, 0));
        
        // Create stat cards
        VBox productCard = createStatCard("Products", "Loading...", "#28a745");
        VBox instanceCard = createStatCard("Instances", "Loading...", "#007bff");
        VBox workflowCard = createStatCard("Workflows", "2", "#ffc107");
        VBox userCard = createStatCard("Users", "10", "#17a2b8");
        
        statsBox.getChildren().addAll(productCard, instanceCard, workflowCard, userCard);
        
        // Load actual stats asynchronously
        loadQuickStats(productCard, instanceCard);
        
        return statsBox;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-border-color: " + color + "; -fx-border-width: 0 0 3 0; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(150);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private void loadQuickStats(VBox productCard, VBox instanceCard) {
        // This would typically be done in a background thread
        // For now, we'll use placeholder values
        ((Label) productCard.getChildren().get(1)).setText("24");
        ((Label) instanceCard.getChildren().get(1)).setText("45");
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1 0 0 0;");
        
        Label statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label versionLabel = new Label("Version 1.0.0");
        versionLabel.setStyle("-fx-text-fill: #666666; -fx-font-size: 12px;");
        
        statusBar.getChildren().addAll(statusLabel, spacer, versionLabel);
        return statusBar;
    }
    
    private void handleNavigationSelection(String selectedItem) {
        // Clear current content
        contentArea.getChildren().clear();
        
        // Create content based on selection
        switch (selectedItem) {
            case "All Products":
                showProductList();
                break;
            case "Product Instances":
                showProductInstances();
                break;
            case "Default Stocking":
            case "Extended Stocking":
                showWorkflowView(selectedItem);
                break;
            case "Workflow Status Report":
                showWorkflowReport();
                break;
            case "Customer Report":
                showCustomerReport();
                break;
            default:
                showDefaultContent(selectedItem);
        }
    }
    
    private void showProductList() {
        Label titleLabel = new Label("Product Management");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        // Create product table (simplified for now)
        TableView<String> productTable = new TableView<>();
        TableColumn<String, String> nameCol = new TableColumn<>("Product Name");
        TableColumn<String, String> numberCol = new TableColumn<>("Product Number");
        TableColumn<String, String> groupCol = new TableColumn<>("Group");
        
        productTable.getColumns().addAll(nameCol, numberCol, groupCol);
        productTable.setPlaceholder(new Label("No products to display"));
        
        VBox.setVgrow(productTable, Priority.ALWAYS);
        contentArea.getChildren().addAll(titleLabel, productTable);
    }
    
    private void showProductInstances() {
        Label titleLabel = new Label("Product Instances");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        // Create instance table
        TableView<String> instanceTable = new TableView<>();
        TableColumn<String, String> serialCol = new TableColumn<>("Serial Number");
        TableColumn<String, String> productCol = new TableColumn<>("Product");
        TableColumn<String, String> customerCol = new TableColumn<>("Customer");
        TableColumn<String, String> stateCol = new TableColumn<>("State");
        
        instanceTable.getColumns().addAll(serialCol, productCol, customerCol, stateCol);
        instanceTable.setPlaceholder(new Label("No instances to display"));
        
        VBox.setVgrow(instanceTable, Priority.ALWAYS);
        contentArea.getChildren().addAll(titleLabel, instanceTable);
    }
    
    private void showWorkflowView(String workflowName) {
        Label titleLabel = new Label("Workflow: " + workflowName);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label descLabel = new Label("Workflow states and transitions will be displayed here.");
        descLabel.setStyle("-fx-text-fill: #666666;");
        
        contentArea.getChildren().addAll(titleLabel, descLabel);
    }
    
    private void showWorkflowReport() {
        Label titleLabel = new Label("Workflow Status Report");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label descLabel = new Label("Workflow status statistics and reports will be displayed here.");
        descLabel.setStyle("-fx-text-fill: #666666;");
        
        contentArea.getChildren().addAll(titleLabel, descLabel);
    }
    
    private void showCustomerReport() {
        Label titleLabel = new Label("Customer Report");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label descLabel = new Label("Customer-related reports and statistics will be displayed here.");
        descLabel.setStyle("-fx-text-fill: #666666;");
        
        contentArea.getChildren().addAll(titleLabel, descLabel);
    }
    
    private void showDefaultContent(String selectedItem) {
        Label titleLabel = new Label(selectedItem);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5aa0;");
        
        Label descLabel = new Label("Content for " + selectedItem + " is not yet implemented.");
        descLabel.setStyle("-fx-text-fill: #666666;");
        
        contentArea.getChildren().addAll(titleLabel, descLabel);
    }
}