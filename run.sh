#!/bin/bash

# HappyHeal Product Workflow Management - Run Script
# This script helps run the application when Maven is not available

echo "HappyHeal Product Workflow Management"
echo "===================================="

# Check for Java
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    echo "Please install Java 17 or higher to run this application"
    exit 1
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | grep "version" | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ] && [ "$JAVA_VERSION" != "1" ]; then
    echo "Warning: Java 17 or higher is recommended"
fi

echo "Java version: $(java -version 2>&1 | head -n 1)"

# Check if data.json exists
if [ ! -f "data.json" ]; then
    echo "Error: data.json not found in current directory"
    echo "Please ensure you're running from the project root directory"
    exit 1
fi

# Check if source files exist
if [ ! -d "src/main/java" ]; then
    echo "Error: Source directory not found"
    echo "Please ensure you're running from the project root directory"
    exit 1
fi

echo ""
echo "Project structure verified."
echo ""
echo "To build and run this application, you need:"
echo "1. Maven 3.6+ (for building)"
echo "2. JavaFX libraries (for runtime)"
echo ""
echo "Build commands:"
echo "  mvn clean compile    # Compile the project"
echo "  mvn javafx:run       # Run the application"
echo "  mvn clean package    # Create executable JAR"
echo ""
echo "Manual compilation (if Maven not available):"
echo "  javac -cp 'lib/*' -d target/classes src/main/java/com/happyheal/**/*.java"
echo ""
echo "Default login credentials:"
echo "  Username: t.kehl"
echo "  Password: 123abc"
echo ""
echo "Features implemented:"
echo "  ✓ JavaFX User Interface with login"
echo "  ✓ SQLite Database with automatic schema creation"
echo "  ✓ JSON data import from data.json"
echo "  ✓ Mustache templating for workflow actions"
echo "  ✓ Product and workflow management"
echo "  ✓ Hierarchical product group structure"
echo "  ✓ Workflow state transitions"
echo "  ✓ Professional UI styling"
echo ""
echo "Note: This application requires JavaFX runtime libraries."
echo "If running with a standalone JRE, ensure JavaFX modules are available."