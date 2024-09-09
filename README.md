# 🎬 Moviever Backend

This is the backend service for a **Moviever**. The backend is built with **Java (Spring Boot)** and features user authentication, film reviews, user watchlist and rating management. It also leverages **PostgreSQL** for data storage and **Elasticsearch** for efficient search capabilities.

## 🚀 Features

- **User Authentication & Authorization:** Uses JWT tokens for secure user management.
- **Film & Review Management:** CRUD operations for films and reviews with many-to-many relationships.
- **Like & Rate Films:** Users can like and rate films, with these interactions dynamically reflected in the database.
- **Search Functionality:** Elasticsearch allows fast and efficient searching of films and reviews.
- **Cloudinary Cloud Storage:**: Efficient media storage and management, enabling seamless uploading and serving of images for films and user profiles.
- **Scalable Architecture:** Optimized for performance with asynchronous processing and efficient database queries.
- **Redis Cache**: Used for improving performance by caching frequently accessed data.


## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.3.1**
  - Spring Data JPA
  - Spring Security (JWT)
  - Spring Web
  - Spring Data Redis
  - Spring Mail
  - Spring Validation
- **Spring Kafka 3.2.1**
- **PostgreSQL 16.4**
- **Elasticsearch 8.15.0**
- **Clouidinary API (for media storage)**
- **Hibernate ORM**
- **Maven** for dependency management

## 📦 Setup & Installation

### Prerequisites

- Java 21
- Maven
- PostgreSQL
- Docker
- Kafka
- Elasticsearch
- Redis

### docker-compose.yml
![docker-compose.yml](https://github.com/erkutoguz/moviever-backend/blob/main/docker-compose.yml.jpg)

### application.yml
![application.yml](https://github.com/erkutoguz/moviever-backend/blob/main/application.yml.jpg)

