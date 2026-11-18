# Payment System

A pet project that simulates a P2P money transfer platform built with microservices. Users can register, create accounts in different currencies, deposit and withdraw money, and send money to other users. Transfers are processed asynchronously through Kafka.

Tech stack: Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, Apache Kafka (Aiven), PostgreSQL, Maven, Docker, Render.

## Deployed Services

The services run on Render's free tier and sleep after 15 minutes of inactivity. After a pause, the first request may fail with an error while the service starts. Just resend it right away (the response may take up to a minute).

- User Service: https://user-service-utq8.onrender.com
- Account Service: https://account-service-ihpe.onrender.com
- Payment Service: https://payment-service-v1r9.onrender.com
- Notification Service: https://notification-service-sqc6.onrender.com

## Services

- user-service: registration, login (JWT), profile update, user deactivation
- account-service: accounts per currency (USD, EUR, GBP), deposits, withdrawals, transaction history, executes transfers
- payment-service: accepts transfer requests and tracks their status (PENDING, COMPLETED, FAILED)
- notification-service: consumes payment results and notifies users
- shared-contracts: shared Kafka events and enums

Each service has its own database schema.

## Kafka Topics

- user-events: user created / deactivated (user-service -> account-service, payment-service)
- payment-initiated-topic: new transfer (payment-service -> account-service)
- payment-result-topic: transfer result (account-service -> payment-service, notification-service)

## Key Features

- Asynchronous transfers through Kafka
- Idempotency key for deposits and withdrawals
- Services keep a local copy of user status, so transfers to deactivated users are rejected
- JWT authentication
- Transaction ledger for every balance change

## API Overview

All endpoints except register and login require the header:

```
Authorization: Bearer <token>
```

### User Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /users | Register a new user |
| POST | /users/login | Log in and get a JWT token |
| GET | /users/profile | Get the current user's profile |
| PATCH | /users/update | Update email and/or password |
| POST | /users/deactivate | Deactivate the current user |

**Register**

```
POST /users
```
```json
{
  "fullName": "Inna Korniichuk",
  "email": "korniichukinna@gmail.com",
  "password": "Password123"
}
```

**Login**

```
POST /users/login
```
```json
{
  "email": "korniichukinna@gmail.com",
  "password": "Password123"
}
```

**Update** (email and password are optional, currentPassword is required)

```
PATCH /users/update
```
```json
{
  "": "korniichukinna@gmail.com",
  "password": "newPassword123",
  "currentPassword": "Password123"
}
```

### Account Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /accounts | Create an account in a currency |
| GET | /accounts | List the current user's accounts |
| POST | /accounts/deposit | Deposit money |
| POST | /accounts/withdraw | Withdraw money |
| GET | /accounts/{id}/history | Transaction history of an account |

**Create account**

```
POST /accounts
```
```json
{
  "currency": "USD"
}
```

**Deposit**

```
POST /accounts/deposit
Idempotency-Key: 3f2b8c1e-5d4a-4a9e-9c1b-7a6e2d8f0b11
```
```json
{
  "amount": 100.00,
  "currency": "USD"
}
```

**Withdraw**

```
POST /accounts/withdraw
Idempotency-Key: 9a7d4e22-1c3b-4f6a-8e05-2b9c7d1a4f33
```
```json
{
  "amount": 30.00,
  "currency": "USD"
}
```

**Idempotency-Key** (optional header for deposit and withdraw)

Use a unique value for every new operation, for example a UUID. If the same request is sent again with the same key (for example, after a network error or a double click), the operation is not repeated and the current account state is returned. To make another deposit or withdrawal, use a new key.

### Payment Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /payments/transfer | Send money to another user |
| GET | /payments/{id} | Get transaction status |
| GET | /payments/history | Transaction history of the current user |

**Transfer**

```
POST /payments/transfer
```
```json
{
  "recipientUserId": 16,
  "amount": 25.00,
  "currency": "USD"
}
```

**Response (202 Accepted)**

```json
{
  "id": 7,
  "amount": 25.00,
  "currency": "USD",
  "status": "PENDING",
  "sourceAccountId": 17,
  "destinationAccountId": 16
}
```

The status changes to COMPLETED or FAILED after the account service processes the transfer.

### Notification Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /notifications | Get notifications of the current user |

```
GET /notifications
```

Notifications are created automatically when a transfer is completed or fails.

### Error Responses

- 400: invalid input (for example, transfer to yourself)
- 404: transaction not found
- 422: recipient not found or inactive
## How to Test

1. Register two users and log in to get a token
2. Create an account for each user
3. Deposit money to the first user
4. POST /payments/transfer to send money to the second user
5. GET /payments/{id} to check the status
