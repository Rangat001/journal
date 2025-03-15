# Journal Application

A secure and interactive web application for personal journaling with sentiment tracking.

## 📌 Overview

This Journal application is a full-stack web application that allows users to create, manage, and track their personal journal entries along with their emotional states. Built with Java Spring Boot backend and HTML/JavaScript frontend, it provides a secure and user-friendly journaling experience.

## 🚀 Features

- **Secure Authentication**: JWT-based user authentication system
- **Journal Entries Management**: Create, read, update, and delete journal entries
- **Sentiment Tracking**: Track your mood with each journal entry
- **Real-time Updates**: Dynamic content updates without page refresh
- **User Profiles**: Personalized user experience
- **Daily Quotes**: Motivational quotes for daily inspiration
- **Data Persistence**: MongoDB for reliable data storage
- **Caching**: Redis for improved performance

## 💻 Technology Stack

### Backend (47%)
- Java Spring Boot
- Spring Security
- JWT Authentication
- MongoDB Database
- Redis Cache
- RESTful API

### Frontend (53%)
- HTML (32.9%)
- JavaScript (15%)
- CSS (5.1%)
- Responsive Design

## 🔧 Project Structure

```
journal/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/rgt/journal/
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── config/
│   │   │       └── security/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   │           └── RGT/
│   │               └── sonet/
│   │                   ├── index.html
│   │                   ├── index.js
│   │                   └── style.css
│   └── test/
└── pom.xml
```

## 📋 Database Setup

### MongoDB Setup

1. **Install MongoDB**
   ```bash
   # For Ubuntu
   sudo apt-get install mongodb

   # For MacOS using Homebrew
   brew install mongodb-community
   ```

2. **Start MongoDB Service**
   ```bash
   # For Ubuntu
   sudo service mongodb start

   # For MacOS
   brew services start mongodb-community
   ```

3. **Create Database and User**
   ```javascript
   # Connect to MongoDB shell
   mongosh

   # Create database
   use journaldb

   # Create user
   db.createUser({
     user: "journaluser",
     pwd: "your_secure_password",
     roles: [{ role: "readWrite", db: "journaldb" }]
   })
   ```

4. **Verify Connection**
   ```bash
   mongosh --host localhost --port 27017 -u journaluser -p your_secure_password --authenticationDatabase admin
   ```

### Redis Setup

1. **Install Redis**
   ```bash
   # For Ubuntu
   sudo apt-get install redis-server

   # For MacOS
   brew install redis
   ```

2. **Start Redis Service**
   ```bash
   # For Ubuntu
   sudo service redis-server start

   # For MacOS
   brew services start redis
   ```

## 🚀 Getting Started

1. **Prerequisites**
   - Java 11 or higher
   - Maven
   - MongoDB 4.4+
   - Redis Server
   - Modern web browser

2. **Configuration**
   - Update `application.yml` with your MongoDB credentials
   - Configure Redis password if needed
   - Set your JWT secret key

3. **Installation**
   ```bash
   # Clone the repository
   git clone https://github.com/Rangat001/journal.git

   # Navigate to project directory
   cd journal

   # Install dependencies
   mvn install

   # Run the application
   mvn spring-boot:run
   ```

4. **Access the Application**
   - Open your browser and navigate to `http://localhost:8080`
   - Register a new account or login with existing credentials

## 💡 Usage

1. **Creating a Journal Entry**
   - Click on "Create New Journal Entry"
   - Fill in the title and content
   - Select your current mood
   - Click "Save Entry"

2. **Managing Entries**
   - View all entries in the journal list
   - Edit entries using the edit button
   - Delete unwanted entries
   - Track mood patterns over time

## 🔐 Security

The application implements several security measures:
- JWT-based authentication
- Password encryption
- Secure session management
- MongoDB authentication
- Redis password protection

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## 👤 Author

- GitHub: [@Rangat001](https://github.com/Rangat001)

## ✨ Acknowledgments

- Spring Boot community
- MongoDB community
- Redis community
- All contributors and users of the application

---
Last Updated: 2025-03-15 12:30:50 UTC
