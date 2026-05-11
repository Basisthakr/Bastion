# Bastion

A private, end-to-end encrypted messaging backend built with Spring Boot. Bastion requires no email address or phone number. Identity is based entirely 
on cryptographic key pairs. The server never stores passwords or readable messages.

## How it works

### Identity and authentication
When a user registers, their device generates an Ed25519 key pair locally. Only the public key is sent to the server along with a chosen username. 
The private key never leaves the device.

Login uses challenge-response authentication:
1. The client requests a challenge from the server
2. The server generates a random nonce and stores it with a 120-second expiry
3. The client signs the nonce with their private key and sends the signature
4. The server verifies the signature using the stored public key
5. On success, a JWT is issued for the session

No password is ever stored or transmitted.

### Messaging
Messages are encrypted on the sender's device before being sent. 
The server stores and forwards ciphertext only. It has no ability to read 
message content. Each message includes a nonce required for decryption, 
which only the recipient's private key can perform.

## Tech stack

- Java 21
- Spring Boot 4
- Spring Security (JWT filter chain, stateless sessions)
- Spring WebSocket (STOMP)
- PostgreSQL (via Spring Data JPA / Hibernate)
- JJWT 0.12.x
- Lombok

## Architecture
Client device
│  generates key pair locally
│  encrypts messages before sending
▼
Spring Boot API
├── Auth Service       (challenge-response, JWT)
├── Messaging Service  (store and forward ciphertext)
├── User Service       (username lookup)
└── WebSocket Service  (STOMP real-time delivery)
▼
PostgreSQL
├── users             (id, username, public_key)
├── messages          (ciphertext, nonce, delivery_status)
└── auth_challenges   (nonce, expiry, used)

## API endpoints

### Auth
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/auth/register | Register with username and public key |
| POST | /api/auth/challenge | Request a login challenge nonce |
| POST | /api/auth/verify | Submit signed nonce, receive JWT |

### Messaging
| Method | Path | Description |
|--------|------|-------------|
| POST | /api/message/send | Send an encrypted message |
| GET | /api/message/conversation/{contactId} | Fetch conversation history |

### Users
| Method | Path | Description |
|--------|------|-------------|
| GET | /api/user/search?username= | Find a user by exact username |

All messaging and user endpoints require `Authorization: Bearer <token>` header.

## Running locally

**Prerequisites:** Java 17, PostgreSQL

1. Clone the repository
2. Create a PostgreSQL database named `Bastion`
3. Copy `application.properties` and set your values:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/Bastion
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=your_base64_encoded_secret
jwt.expiry.hours=24
```
4. Run the application — Hibernate will create tables automatically on first start
5. Use Postman or any HTTP client to interact with the API

## Design decisions

**No email or phone required** — reducing the personal information collected 
reduces the damage of a data breach. A username and public key are sufficient 
for identity.

**Private key never transmitted** — the server cannot impersonate users, 
cannot decrypt messages, and a compromised database reveals no credentials.

**Ciphertext-only storage** — even with full database access, an attacker 
cannot read message content without the recipient's private key.

**Messages deleted after delivery** — minimises data retained on the server. 
What isn't stored cannot be leaked. (Planned — currently messages are retained 
for history retrieval.)

**Stateless JWT sessions** — no server-side session storage required. 
Scales horizontally without shared session state.

## Planned improvements

- BIP39 recovery phrase for private key regeneration on new devices
- Key rotation for compromised key pairs
- Pre-keys and X3DH for forward secrecy
- Token blacklist for proper logout
- Flyway for versioned database migrations
- RabbitMQ to replace the in-memory STOMP message broker
- Group messaging
- End-to-end encrypted media attachments

Note : This is a draft readme. Changes will be made frequently.
