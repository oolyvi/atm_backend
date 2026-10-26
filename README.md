# ATM Monolithic

A Spring Boot backend simulating a bank ATM system: customers, accounts,
cards, ATM terminals with cash cassettes, transactions, and notifications.
Includes a simple web UI and a REST API.

> ⚠️ Demo project. Endpoints are intentionally open (no auth enforced) and
> seeded credentials are for development only. Not for production use.

# Tech Stack
| Category | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4.1 |
| **REST API** | Spring Web MVC |
| **Persistence** | Spring Data JPA, PostgreSQL 17 |
| **Migrations** | Flyway |
| **Security** | Spring Security |
| **Validation** | Jakarta Bean Validation |
| **Real-time** | Spring WebSocket |
| **Notifications** | Spring Mail (SMTP) |
| **Frontend** | Thymeleaf, HTML |
| **Build** | Maven (via Maven Wrapper) |
| **Testing** | JUnit 5, Mockito, Spring Boot Test |
| **Containerization** | Docker, Docker Compose |
| **API testing** | Postman |
| **Version control** | Git, GitHub |

## Features

**Authentication & Cards**
- Card number + PIN authentication at the ATM
- PINs stored as salted hashes only; card numbers stored masked
- Card status validation (active, expired, blocked)
- Secure PIN change
- Block / unblock cards and accounts

**Customers & Accounts**
- Customer and account CRUD
- Multiple accounts and cards per customer
- Balance inquiry by card
- Configurable daily limit per account

**Transactions**
- Atomic debit/credit and account-to-account transfers
- Idempotency keys to prevent duplicate execution
- Refunds as reversal entries, so the ledger is never deleted from
- Transaction history with pagination, date and account filters
- Receipt data for each transaction

**ATM & Cash Management**
- Terminal registration and status management (active, offline, maintenance)
- Cash inventory tracked per denomination per terminal
- Withdrawals dispensed with an optimal bill mix
- Low-cash alerts when a denomination drops below its threshold

**Administration**
- Admin login and profile management
- Reset card PINs, block/unblock cards
- Change terminal status, adjust account limits

**Notifications**
- Email notifications (withdrawal and low-cash alerts)
- Real-time notifications over WebSocket

## Getting Started

**Prerequisites:** JDK 21+, Docker

1. Start PostgreSQL: `docker compose up -d postgres`
2. Run the app: `./mvnw spring-boot:run`
3. Open: http://localhost:8081
4. To reset the database: `docker compose down -v`
5. Replace the placeholder email addresses in the application configuration or source code with real email addresses to test the notification service

## Table of Contents
1. [General Information](#general-information)
2. [Admin Controller (`/admin`)](#1-admin-controller)
3. [Account Controller (`/account`)](#2-account-controller)
4. [ATM & Cash Controller (`/atm`, `/cash`)](#3-atm--cash-controller)
5. [ATM Terminals Controller (`/atm/terminal`)](#4-atm-terminals-controller)
6. [Card Controller (`/card`)](#5-card-controller)
7. [Customer Controller (`/customer`)](#6-customer-controller)
8. [Notification Controller (`/notification`)](#7-notification-controller)
9. [Transaction Controller (`/transaction`)](#8-transaction-controller)

---

# API Reference
# 1. Admin Controller

Endpoints for administrative tasks, terminal/card state updates, and management.

### 1.1 Authenticate Admin
* **Method:** `POST`
* **Path:** `/admin/login`
* **Description:** Authenticates an administrative user.
* **Request Body:**
  ```json
  {
    "username": "admin",
    "password": "1234"
  }
  ```

### 1.2 Update Admin Profile
* **Method:** `PUT`
* **Path:** `/admin/{adminId}/profile`
* **Description:** Updates the profile info for a specified admin ID.
* **Query Parameters:**
    - `username` (string, required): New username.
    - `email` (string, required): New email address.
* **Request Body:** None

### 1.3 Update Account Daily Limit
* **Method:** `PATCH`
* **Path:** `/admin/accounts/{accountId}/limit`
* **Description:** Adjusts the daily transaction/withdrawal limit on an account.
* **Query Parameters:**
    - `limit` (number, required): New daily limit amount (e.g., `999`).
* **Request Body:** None

### 1.4 Change Terminal Status
* **Method:** `PATCH`
* **Path:** `/admin/terminals/{terminalId}/status`
* **Description:** Updates the status of an ATM terminal (e.g., `OFFLINE`, `ACTIVE`).
* **Query Parameters:**
    - `status` (string, required): Status string.
* **Request Body:** None

### 1.5 Unblock Card
* **Method:** `POST`
* **Path:** `/admin/cards/{cardId}/unblock`
* **Description:** Unblocks a blocked payment card.
* **Request Body:** None

### 1.6 Block Card
* **Method:** `POST`
* **Path:** `/admin/cards/{cardId}/block`
* **Description:** Administrative action to block a payment card.
* **Request Body:** None

### 1.7 Force-Reset Card PIN Hash
* **Method:** `POST`
* **Path:** `/admin/cards/{cardId}/reset-pin`
* **Description:** Forcefully resets the PIN hash for a target card.
* **Query Parameters:**
    - `temporaryPinHash` (string, required): The replacement hash value.
* **Request Body:** None

---

# 2. Account Controller

Endpoints for managing customer bank accounts.

### 2.1 List Accounts
* **Method:** `GET`
* **Path:** `/account/all`
* **Description:** Retrieves all bank accounts.

### 2.2 Get Account by ID
* **Method:** `GET`
* **Path:** `/account/{id}`
* **Description:** Fetches details of a specific account by its primary key.

### 2.3 Create Account
* **Method:** `POST`
* **Path:** `/account/add`
* **Description:** Creates a new bank account linked to a customer.
* **Request Body:**
  ```json
  {
    "accountNumber": "AZ100000000000000005",
    "balance": "300",
    "dailyLimit": "1000",
    "accountType": "CHECKING",
    "status": "ACTIVE",
    "customer": {
      "id": 1
    }
  }
  ```

### 2.4 Replace Account
* **Method:** `PUT`
* **Path:** `/account/{id}`
* **Description:** Replaces an existing account object.
* **Request Body:**
  ```json
  {
    "accountNumber": "AZ100000000000000001",
    "balance": "500",
    "dailyLimit": "1000",
    "accountType": "CHECKING",
    "status": "ACTIVE",
    "customer": {
      "id": 1
    }
  }
  ```

### 2.5 Delete Account
* **Method:** `DELETE`
* **Path:** `/account/{id}`
* **Description:** Removes an account by ID.

### 2.6 Unblock Account
* **Method:** `POST`
* **Path:** `/account/{accountId}/unblock`
* **Description:** Removes block status on an account.

### 2.7 Block Account
* **Method:** `POST`
* **Path:** `/account/{accountId}/block`
* **Description:** Suspends or blocks an account.

---

# 3. ATM & Cash Controller

Endpoints for card PIN verification, cash withdrawals, and balance inquiries via the ATM client interface.

### 3.1 ATM Login / Verify Card PIN
* **Method:** `POST`
* **Path:** `/atm/login`
* **Description:** Verifies card credentials and PIN at the terminal.
* **Request Body:**
  ```json
  {
    "cardNumber": "4532678912348812",
    "pin": "1234"
  }
  ```

### 3.2 Get Balance by Card Number
* **Method:** `GET`
* **Path:** `/atm/balance`
* **Description:** Returns the available balance for the specified card.
* **Query Parameters:**
    - `cardNumber` (string, required): Card number (e.g., `4000000000000001`).

### 3.3 Withdraw Cash from ATM
* **Method:** `POST`
* **Path:** `/cash/withdraw`
* **Description:** Dispenses physical currency from a terminal and updates account balances/cassettes.
* **Query Parameters:**
    - `atmId` (number, required): ID of the ATM terminal dispensing cash.
    - `amount` (number, required): Total cash amount requested.

---

# 4. ATM Terminals Controller

Endpoints for onboarding and monitoring physical terminal hardware and inventory.

### 4.1 Create ATM Terminal
* **Method:** `POST`
* **Path:** `/atm/terminal/add`
* **Description:** Registers a new terminal along with initial bill counts across all cassettes.
* **Request Body:**
  ```json
  {
    "terminalCode": "ATM-KICK-01",
    "location": "Khaldoun 25",
    "status": "ACTIVE",
    "ipAddress": "127.0.0.1",
    "softwareVersion": "v2.1.1",
    "cassette1Count": "0",
    "cassette5Count": "0",
    "cassette10Count": "0",
    "cassette20Count": "0",
    "cassette50Count": "2",
    "cassette100Count": "0",
    "cassette200Count": "0",
    "cassette500Count": "0"
  }
  ```

### 4.2 List ATM Terminals
* **Method:** `GET`
* **Path:** `/atm/terminal/all`
* **Description:** Retrieves all registered ATM terminals.

### 4.3 Get Terminal by ID
* **Method:** `GET`
* **Path:** `/atm/terminal/{id}`
* **Description:** Returns details for a specific ATM terminal.

---

# 5. Card Controller

Endpoints for managing customer debit/credit cards and card PIN settings.

### 5.1 List Cards
* **Method:** `GET`
* **Path:** `/card/all`
* **Description:** Returns all cards in the system.

### 5.2 Get Card by ID
* **Method:** `GET`
* **Path:** `/card/{id}`
* **Description:** Retrieves card details by ID.

### 5.3 Create Card
* **Method:** `POST`
* **Path:** `/card/add`
* **Description:** Issues a new card and links it to a customer and an account.
* **Request Body:**
  ```json
  {
    "cardNumber": "4532678912341221",
    "pin": "1234",
    "cardType": "DEBIT",
    "customerId": 1,
    "accountId": 1
  }
  ```

### 5.4 Verify Card PIN
* **Method:** `POST`
* **Path:** `/card/{id}/verify-pin`
* **Description:** Validates input PIN against stored credentials for card with `{id}`.
* **Request Body:**
  ```json
  {
    "pin": "1234"
  }
  ```

### 5.5 Change Card PIN
* **Method:** `POST`
* **Path:** `/card/{cardId}/pin-change`
* **Description:** Updates PIN from old value to new value.
* **Request Body:**
  ```json
  {
    "oldPin": "1234",
    "newPin": "4321"
  }
  ```

---

# 6. Customer Controller

Endpoints for customer profile registration, management, and filtering.

### 6.1 List Customers
* **Method:** `GET`
* **Path:** `/customer/all`
* **Description:** Retrieves all customer profiles.

### 6.2 Get Customer by ID
* **Method:** `GET`
* **Path:** `/customer/{id}`
* **Description:** Gets a customer profile by primary key.

### 6.3 Create Customer
* **Method:** `POST`
* **Path:** `/customer/add`
* **Description:** Registers a new customer profile.
* **Request Body:**
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "+994501234567",
    "dateOfBirth": "2000-01-01",
    "nationalId": "AZ1234567",
    "passwordHash": "replace-with-hash",
    "status": "ACTIVE"
  }
  ```

### 6.4 Replace Customer
* **Method:** `PUT`
* **Path:** `/customer/{id}`
* **Description:** Fully updates an existing customer profile.
* **Request Body:**
  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "phoneNumber": "+994501234567",
    "dateOfBirth": "2000-01-01",
    "nationalId": "AZ1234567",
    "passwordHash": "replace-with-hash",
    "status": "ACTIVE"
  }
  ```

### 6.5 Delete Customer
* **Method:** `DELETE`
* **Path:** `/customer/{id}`
* **Description:** Deletes a customer profile.

### 6.6 List Customers by Status
* **Method:** `GET`
* **Path:** `/customer/status/{status}`
* **Description:** Filters customers by operational status (e.g., `ACTIVE`, `BLOCKED`).

---

# 7. Notification Controller

Endpoints for dispatching messaging notifications.

### 7.1 Send Notification
* **Method:** `POST`
* **Path:** `/notification/send`
* **Description:** Sends outbound notification (Email/SMS).
* **Request Body:**
  ```json
  {
    "type": "EMAIL",
    "recipient": "test@example.com",
    "title": "Title",
    "message": "Perfect message!"
  }
  ```

---

# 8. Transaction Controller

Endpoints for ledger execution, inter-account transfers, receipts, and refund requests.

### 8.1 Execute Account Transaction
* **Method:** `POST`
* **Path:** `/transaction/execute`
* **Required Headers:** `Idempotency-Key` (UUID)
* **Description:** Debits or credits a target account.
* **Request Body:**
  ```json
  {
    "accountId": 1,
    "amount": 150.0,
    "type": "DEBIT"
  }
  ```

### 8.2 Get Transaction History
* **Method:** `GET`
* **Path:** `/transaction`
* **Description:** Queries historical transactions with optional filtering and pagination.
* **Query Parameters:**
    - `accountId` (number, required): Account ID.
    - `startDate` (ISO DateTime, optional): Start window (e.g., `2026-01-01T00:00:00`).
    - `endDate` (ISO DateTime, optional): End window (e.g., `2026-12-31T23:59:59`).
    - `page` (number, optional): Page index (default: `0`).
    - `size` (number, optional): Page size (default: `10`).

### 8.3 Get Transaction by ID
* **Method:** `GET`
* **Path:** `/transaction/{id}`
* **Description:** Retrieves transaction details by transaction ID.

### 8.4 Refund Transaction
* **Method:** `POST`
* **Path:** `/transaction/{id}/refund`
* **Required Headers:** `Idempotency-Key` (UUID)
* **Description:** Reverses a prior transaction by ID.

### 8.5 Get Transaction Receipt
* **Method:** `GET`
* **Path:** `/transaction/{id}/receipt`
* **Description:** Retrieves formatted receipt or receipt data for a transaction.

### 8.6 Transfer Between Accounts
* **Method:** `POST`
* **Path:** `/transaction/transfer`
* **Required Headers:** `Idempotency-Key` (UUID)
* **Description:** Transfers funds between source and destination accounts.
* **Request Body:**
  ```json
  {
    "sourceAccountId": 1,
    "destinationAccountId": 2,
    "amount": 50.0
  }