# E-Commerce Store Backend API

A RESTful Spring Boot backend for an e-commerce platform with JWT authentication, role-based access control, and product management capabilities.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Configuration](#configuration)
- [Deployment](#deployment)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)

 ✨ Features

 User Management
- **Two User Roles**: Buyers and Sellers with distinct permissions
- **JWT Authentication**: Secure token-based authentication
- **BCrypt Encryption**: Password hashing for security
- **10-hour Token Expiration**: Automatic session management

 Product Management
- **CRUD Operations**: Full product lifecycle management
- **Ownership Verification**: Sellers can only modify their own products
- **Public Product Browsing**: Anyone can view products
- **Seller Dashboard**: View and manage personal product listings

 Security
- **Role-Based Access Control (RBAC)**: Fine-grained permissions
- **CORS Configuration**: Cross-origin resource sharing enabled
- **Protected Endpoints**: JWT validation on sensitive operations
- **403 Forbidden**: Automatic rejection of unauthorized access

 🛠️ Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 3.x |
| **Language** | Java 17+ |
| **Security** | Spring Security + JWT |
| **Database** | MySQL 8.0 |
| **ORM** | Hibernate/JPA |
| **Build Tool** | Maven |
| **Authentication** | JSON Web Tokens |

## 🏗️ System Architecture

```
┌─────────────────┐
│  Angular Client │
│   (Frontend)    │
└────────┬────────┘
         │ HTTPS/REST
         ▼
┌─────────────────┐
│  Spring Boot    │
│   REST API      │◄──── JWT Filter
└────────┬────────┘
         │ JDBC
         ▼
┌─────────────────┐
│  MySQL Database │
│   (storedb)     │
└─────────────────┘
```

### Key Components
- **Controllers**: Handle HTTP requests and responses
- **Services**: Business logic implementation
- **Repositories**: Database operations using Spring Data JPA
- **Security Config**: JWT authentication and authorization
- **Models**: User, Product, and Role entities

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: 17 or higher
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Git**: For version control

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/kloyalty/api.git
   cd api
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE storedb;
   ```

3. **Configure Database Connection**
   
   Copy the example properties file:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

   Edit `application.properties` with your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/storedb?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at `http://localhost:8080`

### Quick Test

Test if the server is running:
```bash
curl http://localhost:8080/products
```

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/register` | Register new user (buyer/seller) | No |
| POST | `/auth/login` | Login and receive JWT token | No |

**Register Request Example:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "securepassword",
  "role": "SELLER"
}
```

**Login Response Example:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "johndoe",
  "role": "SELLER"
}
```

### Products (Public)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/products` | Get all products | No |
| GET | `/products/{id}` | Get single product | No |

### Products (Protected - Sellers Only)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/products` | Create new product | Yes (SELLER) |
| GET | `/products/my-products` | Get seller's products | Yes (SELLER) |
| PUT | `/products/{id}` | Update own product | Yes (SELLER) |
| DELETE | `/products/{id}` | Delete own product | Yes (SELLER) |

**Create Product Request:**
```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic wireless mouse with USB receiver",
  "price": 29.99,
  "stock": 150
}
```

### Authorization Header Format

All protected endpoints require a JWT token:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🔒 Security

### Authentication Flow

1. User registers or logs in via `/auth/register` or `/auth/login`
2. Server validates credentials and generates JWT token
3. Client stores token (typically in localStorage)
4. Client includes token in `Authorization` header for protected requests
5. Server validates token and grants/denies access

### Role-Based Access Control

| Role | Can Create | Can Read All | Can Update Own | Can Delete Own |
|------|------------|--------------|----------------|----------------|
| **SELLER** | ✅ | ✅ | ✅ | ✅ |
| **BUYER** | ❌ | ✅ | ❌ | ❌ |
| **Public** | ❌ | ✅ | ❌ | ❌ |

### Security Features

- **Password Encryption**: BCrypt with salt
- **Token Expiration**: 10 hours (configurable)
- **CORS Protection**: Configured for specific origins
- **Ownership Verification**: Sellers can only modify their own products
- **SQL Injection Protection**: Parameterized queries via JPA

## ⚙️ Configuration

### Environment Variables

For production deployment, use environment variables:

```bash
export DATABASE_URL=jdbc:mysql://your-db-host:3306/storedb
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key_here
```

### Application Properties

Key configuration options in `application.properties`:

```properties
# Database
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT (configured in code)
jwt.secret=${JWT_SECRET:defaultSecretKey}
jwt.expiration=36000000  # 10 hours in milliseconds
```

## 🌐 Deployment

### Current Deployment

- **Method**: ngrok tunnel (development/testing)
- **URL**: https://principled-untyrannically-giuliana.ngrok-free.dev
- **Note**: URL changes on restart

### Production Deployment Options

Recommended platforms for permanent deployment:

1. **Render.com** (Free tier available)
   ```bash
   # Deploy via GitHub integration
   ```

2. **Railway.app** (Free trial)
   ```bash
   railway login
   railway init
   railway up
   ```

3. **Heroku**
   ```bash
   heroku create your-app-name
   git push heroku main
   ```

4. **AWS Elastic Beanstalk**
   - Package as JAR
   - Deploy via EB CLI or console

### Frontend Integration

This backend works with the Angular frontend:
- **Repository**: [Store Frontend](https://github.com/yourusername/store-frontend)
- **Live Demo**: https://store-frontend-oggdy1jzp-prodefieds-projects.vercel.app

## 🔮 Future Enhancements

### Planned Features

- [ ] **Shopping Cart**: Session-based cart management
- [ ] **Order System**: Complete purchase workflow
- [ ] **Payment Integration**: Stripe/PayPal gateway
- [ ] **Image Upload**: Product image management
- [ ] **Search & Filters**: Advanced product search
- [ ] **Reviews & Ratings**: Customer feedback system
- [ ] **Admin Dashboard**: Analytics and monitoring
- [ ] **Email Notifications**: Order confirmations

### Security Improvements

- [ ] **Rate Limiting**: Prevent API abuse
- [ ] **Two-Factor Authentication (2FA)**
- [ ] **Password Reset**: Secure recovery flow
- [ ] **Token Refresh**: Automatic token renewal
- [ ] **Audit Logging**: Track user actions

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards

- Follow Java naming conventions
- Write unit tests for new features
- Update documentation as needed
- Ensure code passes all existing tests

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📧 Contact

**Project Maintainer**: [Your Name]

- GitHub: [@kloyalty](https://github.com/kloyalty)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- JWT.io for token implementation guidance
- The open-source community

---

**⭐ If you find this project useful, please consider giving it a star!**

Made with ❤️ using Spring Boot
