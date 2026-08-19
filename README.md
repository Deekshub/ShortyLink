# ShortyLink - Premium URL Shortener

ShortyLink is a secure, modern, and lightweight self-hosted URL shortener built with **Spring Boot** and **MySQL**. It allows you to shorten long URLs, protect them with passwords, and generate QR codes automatically for easy sharing.

## ✨ Features

- **Instant URL Shortening**: Create short links in seconds.
- **Custom Alias (Optional)**: Choose your own custom slug for branded links.
- **Password Protection (Optional)**: Secure your short links with a password.
- **Auto-Generated QR Codes**: Instantly scan and share links.
- **Modern Light Theme**: Clean, responsive layout for all screen sizes.
- **Error Handling**: Friendly alerts when aliases are already taken.

## 🛠️ Tech Stack

- **Backend**: Java 17+, Spring Boot (Web, JPA, Thymeleaf, Validation)
- **Database**: MySQL
- **Security**: BCrypt for secure password hashing
- **Frontend**: Custom HTML5, CSS3 (Modern Light Theme), JavaScript (Fetch API)
- **QR Code Generator**: QRServer API

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.x
- MySQL Server

### Database Setup

1. Create a MySQL database named `url_shortener`:
   ```sql
   CREATE DATABASE url_shortener;
   ```
2. Update database credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Running the Application

1. Clone or download the repository.
2. Navigate to the project root directory.
3. Run the application:
   - **Windows (PowerShell)**:
     ```powershell
     ./mvnw.cmd spring-boot:run
     ```
   - **Linux / macOS**:
     ```bash
     chmod +x mvnw
     ./mvnw spring-boot:run
     ```
4. Open your browser and navigate to `http://localhost:8080`.

## 📂 Project Structure

```text
urlshortener/
├── src/
│   ├── main/
│   │   ├── java/com/deekshitha/urlshortener/
│   │   │   ├── config/            # Security & app configuration
│   │   │   ├── controller/        # REST Controllers & View Controllers
│   │   │   ├── dto/               # Data Transfer Objects (Requests/Responses)
│   │   │   ├── entity/            # JPA Entities (UrlMapping)
│   │   │   ├── exception/         # Exception handlers (Duplicate alias, etc.)
│   │   │   ├── repository/        # Spring Data JPA repositories
│   │   │   └── service/           # Business logic & hashing
│   │   └── resources/
│   │       ├── templates/         # Thymeleaf HTML views (index, result)
│   │       └── static/            # Static assets (css/style.css, js/script.js)
```

## 🔒 Security

All passwords set on short URLs are automatically hashed before saving to the database using **BCrypt Strong Hashing**.
