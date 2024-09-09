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

### docker-compose.yml
- services:
  - elasticsearch:
  -  image: docker.elastic.co/elasticsearch/elasticsearch:8.15.0
  -  container_name: moviever_elasticsearch
  -  ports:
  -    - '9200:9200'
  -    - '9300:9300'
  -  environment:
  -    - discovery.type=single-node
  -    - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
  -    - xpack.security.enabled=false
  -  ulimits:
  -    memlock:
  -      soft: -1
  -      hard: -1
  -  networks:
  -    - moviever-backend-network
  -  volumes:
  -    - moviever-elasticsearch-data:/usr/share/elasticsearch/data
      
  - kibana:
   - image: docker.elastic.co/kibana/kibana:8.15.0
   - container_name: moviever_kibana
   - ports:
   -   - "5601:5601"
   - networks:
   -   - moviever-backend-network

  redis:
    image: redis:latest
    container_name: moviever_redis
    ports:
      - '6379:6379'
    networks:
      - moviever-backend-network

  zookeeper:
    image: "docker.io/bitnami/zookeeper:3"
    container_name: moviever_zookeeper
    ports:
      - '2181:2181'
    volumes:
      - "moviever-zookeeper-data:/usr/share/zookeeper/data"
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
    networks:
      - moviever-backend-network
      
  postgres:
    container_name: moviever_postgres
    image: postgres:16.4
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: zer0day
      POSTGRES_DB: moviever_database
    ports:
      - '5431:5432'
    volumes:
      - "moviever-database:/usr/share/database/data"
    networks:
      - moviever-backend-network

  kafka:
    image: "docker.io/bitnami/kafka:2-debian-10"
    container_name: moviever_kafka
    ports:
      - '9092:9092'
    expose:
      - '9093'
    volumes:
      - "moviever-kafka-data:/usr/share/kafka/data"
    environment:
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes
      - KAFKA_ADVERTISED_LISTENERS=INSIDE://kafka:9093,OUTSIDE://localhost:9092
      - KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=INSIDE:PLAINTEXT,OUTSIDE:PLAINTEXT
      - KAFKA_LISTENERS=INSIDE://0.0.0.0:9093,OUTSIDE://0.0.0.0:9092
      - KAFKA_INTER_BROKER_LISTENER_NAME=INSIDE
      - KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181
    depends_on:
      - zookeeper
    networks:
      - moviever-backend-network

networks:
  moviever-backend-network:

volumes:
  moviever-zookeeper-data:
    driver: local
  moviever-elasticsearch-data:
    driver: local
  moviever-kafka-data:
    driver: local
  moviever-database:
    driver: local

### application.yml
spring:
  main:
    allow-bean-definition-overriding: true
    
  user-admin:
    password: password
    email: admin mail
    username: username
    
  application:
    name: moviever-backend
    
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

  ip-info:
    token: ip info token

  kafka:
    bootstrap-servers: localhost:9092

  security:
    JWT_SECRET: jwt secret
    ACCESS_EXPIRATION: 900_000
    REFRESH_EXPIRATION: 86_400_000
    RESET_PASSWORD_EXPIRATION: 900_000

  cloudinary:
    url: cloudinary api uri 

  data:
    elasticsearch:
      client:
        uris:
          - http://localhost:9200
    redis:
      host: localhost
      port: 6379
      timeout: 10000ms
      lettuce:
        pool:
          max-active: 20
          max-wait: 5000ms
          max-idle: 10
          min-idle: 0
          
  cache:
    type: redis
    redis:
      time-to-live: 3600
      cache-null-values: false

  mail:
    host: smtp.gmail.com
    port: 587
    username: smtp server username
    password: smtp server pass
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            
  datasource:
    hikari:
      maximum-pool-size: 10
      idle-timeout: 60000
      connection-timeout: 30000
    url: jdbc:postgresql://localhost:5431/moviever_database
    username: username
    password: password
    driver-class-name: org.postgresql.Driver
    
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
server:
  port: 9991

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always


