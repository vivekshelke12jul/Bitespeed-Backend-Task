# Bitespeed Backend Task: Identity Reconciliation

This is a simple API that allows you to identify a contact based on their email or phone number.
It consolidates multiple contacts with the same phone number or email into one contact and returns the consolidated contact.
for more details refer the [problem statement](https://bitespeed.notion.site/Bitespeed-Backend-Task-Identity-Reconciliation-53392ab01fe149fab989422300423199)

---

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot (Java)
- **Build Tool**: Gradle
- **Database**: PostgreSQL
- **Deployment**: Docker, [Render](https://render.com)

---

## 🚀 Live Demo

> 🌐 https://bitespeed-backend-task-wer5.onrender.com

---

## 🧪 API Specification

### Endpoint: `/identify`

**Method:** `POST`  
**Content-Type:** `application/json`

### ✅ Request Body Example

```json
{
  "email": "secondary@example.com",
  "phoneNumber": "0987654321"
}
```

### ✅ Response Body Example

```json
{
  "contact": {
    "primaryContatctId": 1,
    "emails": ["primary@example.com", "secondary@example.com"],
    "phoneNumbers": ["1234567890", "0987654321"],
    "secondaryContactIds": [2, 3]
  }
}
```

---

## 🧳 How to Run the Project

1. Clone the repository

```bash
git clone https://github.com/vivekshelke12jul/Bitespeed-Backend-Task.git
cd Bitespeed-Backend-Task
```

2. Create a database in PostgreSQL and set the connection details in the application.properties file

```bash

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/identityReconciliationDB}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:vivek}
```

3. Build the project

```bash
./gradlew clean build
```

4. Run the project

```bash
./gradlew bootRun
```