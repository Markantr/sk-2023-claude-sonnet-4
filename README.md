# HappyHeal Product Workflow Management

A JavaFX application for managing product workflows with SQLite database and Mustache templating, built according to ICT Skills assignment specifications.

## Features

- **User Authentication**: Secure login system with user management
- **Product Management**: Hierarchical product group structure with individual product instances
- **Workflow System**: Configurable workflows with multiple state transitions
- **Mustache Templating**: Dynamic content generation for workflow actions
- **Database Integration**: Automatic SQLite database setup and data migration
- **Modern UI**: Clean JavaFX interface with responsive design

## Technology Stack

- **Language**: Java 17+
- **UI Framework**: JavaFX 21
- **Database**: SQLite 3
- **Template Engine**: Mustache Java
- **Build Tool**: Maven
- **JSON Processing**: Jackson

## Project Structure

```
src/main/java/com/happyheal/
├── Main.java                          # Application entry point
├── model/                             # Data model classes
│   ├── AppData.java                   # Root data structure
│   ├── User.java                      # User model
│   ├── ProductGroup.java              # Product group hierarchy
│   ├── Product.java                   # Product model
│   ├── ProductInstance.java           # Product instance model
│   ├── Workflow.java                  # Workflow model
│   ├── WorkflowState.java             # Workflow state model
│   └── WorkflowTransition.java        # Workflow transition model
├── database/                          # Database management
│   ├── DatabaseManager.java           # SQLite connection and schema
│   └── DataImporter.java              # JSON data import
├── ui/                                # User interface
│   ├── MainApplication.java           # JavaFX application
│   └── MainController.java            # Main UI controller
└── workflow/                          # Workflow engine
    ├── MustacheTemplateEngine.java    # Template processing
    └── WorkflowExecutor.java          # Workflow transition execution

src/main/resources/
└── styles.css                        # CSS styling

data.json                             # Initial data file
icons/                                # SVG icons
├── happyheal.svg
├── add.svg
├── internet.svg
├── mail.svg
├── message.svg
└── save.svg
```

## Database Schema

The application automatically creates the following SQLite tables:

- `users` - User authentication data
- `workflows` - Workflow definitions
- `workflow_states` - Individual workflow states
- `workflow_transitions` - State transitions with actions
- `product_groups` - Hierarchical product organization
- `products` - Product definitions
- `product_instances` - Individual product instances with states

## Workflow System

### Supported Transition Types

1. **HTTP**: REST API calls with Mustache templating
2. **File**: File operations (create/append) with templated content
3. **Mail**: Email notifications (logged for demonstration)
4. **MessageBox**: JavaFX dialog notifications
5. **NoAction**: State changes without additional actions

### Mustache Variables

Available in workflow templates:
- `{{type}}` - Product name
- `{{sn}}` - Serial number
- `{{customermail}}` - Customer email
- `{{customername}}` - Customer name
- `{{productnumber}}` - Product number
- `{{purchasedate}}` - Purchase date

## Build and Run

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Building

```bash
# Clean and compile
mvn clean compile

# Create executable JAR
mvn clean package

# Run with JavaFX plugin
mvn javafx:run
```

### Running

```bash
# Using Maven
mvn javafx:run

# Using compiled JAR
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar target/product-workflow-management-1.0.0.jar

# Or simply (if JavaFX is on classpath)
java -jar target/product-workflow-management-1.0.0-shaded.jar
```

### Default Users

The application comes with pre-configured users (from data.json):

| Username | Password | Display Name |
|----------|----------|--------------|
| t.kehl | 123abc | Thomas Kehl |
| u.niederer | 123abc | Ueli Niederer |
| m.boller | 123abc | Micha Boller |
| m.wilson | SecurePwd12! | Michael Wilson |
| s.miller | Passw0rd$ | Sarah Miller |

## Application Flow

1. **Startup**: Database initialization and data import
2. **Login**: User authentication screen
3. **Main Interface**: Navigation tree with content areas
   - Product Groups (hierarchical view)
   - Workflows (Default/Extended Stocking)
   - Product Management (All Products, Instances)
   - Reports (Workflow Status, Customer Reports)

## Data Import

On first run, the application:
1. Creates SQLite database schema
2. Imports users, workflows, and product data from `data.json`
3. Sets up initial workflow states for product instances

## Configuration

### Database

- Database file: `happyheal.db` (created automatically)
- Connection: SQLite JDBC
- Foreign keys: Enabled
- Indexes: Optimized for common queries

### Styling

- CSS file: `src/main/resources/styles.css`
- Theme: Professional blue/white color scheme
- Responsive: Adapts to window resizing

## Development

### Adding New Features

1. **New Models**: Add to `com.happyheal.model` package
2. **Database Changes**: Update `DatabaseManager.createTables()`
3. **UI Components**: Extend `MainController` navigation
4. **Workflow Actions**: Add to `WorkflowExecutor.executeTransitionAction()`

### Testing

The application includes extensive error handling and logging for:
- Database operations
- Template processing
- Workflow execution
- UI interactions

## License

This project is developed as part of the ICT Skills assignment (Session 9, 2023).

## Notes

- SQLite database is created automatically in the project root
- Icons are SVG format (basic JavaFX display support)
- HTTP actions require external API endpoints to be available
- Email actions are logged to console (SMTP configuration would be needed for real sending)
- Mustache templates are validated before execution