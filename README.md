# Priority Queue Service Implementation

### Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Execution](#Execution)
4. [Project Structure](#project-structure)
5. [License](#license)

---

## Introduction

This project is a Queue Service enhancement assignment focusing on:
1. Implementing a priority queue in an existing codebase.
2. Developing a new queue using Upstash as the backend thorugh Redis.

The project includes priority-based request handling, adherence to First-Come-First-Serve (FCFS) within the same priority level, and comprehensive test cases for validation.

---

## Features

- **Priority Queue System**: Requests are assigned numerical priorities for efficient retrieval.
- **InMemoryPriorityQueue**: Enhanced to support priority-based polling and FCFS order.
- **Upstash-Based Queue**: Uses Redis via Upstash HTTP API, with the same priority system.
- **Test Cases**: Comprehensive tests for functionality and edge cases.

---

## Execution
1. Clone the repository:
   ```bash
   git clone https://github.com/tarangchaturvedi/Priority_Queue_Service.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Priority_Queue_Service
   ```
3. Run UnitTests with mvn package:
   ```bash
   mvn test
   ```
---

## Project Structure

```plaintext
├── README.md             # Project documentation
├── pom.xml               # Maven build file
├── src/
│   ├── main/
│   │   ├── java/         
│   │   │   └── com/example/
│   │   │       ├── QueueService.java        # Interface for QueueService.
│   │   │       ├── Message.java        # Class for message objects with necessary attributes and methods.
│   │   │       ├── InMemoryPriorityQueueService.java       # Implementation of InMemory Priority and FCFS based Queue Service.
│   │   │       └── RedisQueueService.java       # Implementation of similar Priority and FCFS based Queue Service using Upstash as backend with Redis.
│   │   └── resources/    # Configuration files (e.g., config.properties)
│   │       └── config.properties
│   └── test/
│       └── java/         # Unit tests
│           └── com/example/
│               ├── InMemoryQueueTest.java       # Unit Tests for InMemory Priority Queue Service.
│               └── UpstashRedisQueueTest.java       # Unit Tests for Upstash API based Priority Queue Service.
└── target/               # Compiled output (generated by Maven)

```
---

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

---
