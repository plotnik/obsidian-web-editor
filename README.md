# Obsidian Web Editor

A Spring Boot web application that provides a browser-based interface for editing Obsidian markdown files. This tool allows you to edit your Obsidian notes through a web textarea, making it convenient to modify markdown files from any device with a browser.

## Features

- 🌐 Web-based markdown editor for Obsidian notes
- 📁 Browse and select Obsidian vaults and subfolders
- 📝 Edit markdown files directly in a textarea
- 🔄 Real-time file updates
- 📋 Session-based state management
- 🎯 Simple and intuitive interface

## Prerequisites

- Java 21 or higher
- Maven (or use the included Maven wrapper)
- An existing Obsidian vault

## Quick Start

1. Clone this repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/obsidian-web-editor.git
   cd obsidian-web-editor
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

3. Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

4. Select your Obsidian vault and start editing!

## Building from Source

To build the application as a JAR file:

```bash
./mvnw clean package
```

The built JAR will be available in the `target/` directory.

## Usage

1. **Select Vault**: Choose your Obsidian vault from the dropdown
2. **Browse Folders**: Navigate through your vault's folder structure
3. **Pick a File**: Select a markdown file from the newest files list
4. **Edit**: Modify the content in the textarea
5. **Save**: Use the update functionality to save your changes

## Technology Stack

- **Spring Boot 3.5.4** - Application framework
- **Spring Web** - Web layer
- **Thymeleaf** - Template engine
- **Java 21** - Programming language
- **Maven** - Build tool

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source. Please check the license file for more details.

## Support

If you encounter any issues or have questions, please open an issue on GitHub.