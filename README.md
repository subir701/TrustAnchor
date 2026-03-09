# ⚓ TrustAnchor v1.0

**TrustAnchor** is a high-performance, privacy-first **Retrieval-Augmented Generation (RAG) orchestrator** built with **Java 21** and **Spring Boot 3.4.2**.

It enables you to **chat with your local PDF documents with 100% privacy**, running entirely on your local machine using **Ollama**.

What makes TrustAnchor unique is its **Tiered Caching Architecture**, designed to provide **near-instant responses even on resource-constrained hardware (e.g., 8GB RAM)** by minimizing expensive LLM and vector database operations.

---

# 📚 Table of Contents

- [Introduction](#introduction)
- [Key Features](#key-feature)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Frontend](#frontend)
- [Usage](#usage)
- [Testing](#testing)
- [Configuration](#configuration)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)
- [Contributors](#contributors)
- [License](#license)

---

# Introduction

Large Language Models are powerful but expensive to run locally. Every query can trigger:

- Vector database retrieval
- Embedding generation
- LLM inference

On machines with limited RAM or CPU resources, this leads to **slow response times**.

**TrustAnchor solves this with a tiered caching strategy** that prioritizes fast lookups before invoking expensive operations. The system intelligently caches both **exact queries** and **semantic equivalents**, drastically reducing the need for repeated LLM calls.

The result is a **fast, private, and hardware-efficient RAG system**.

---
![http://url/to/img.png](https://github.com/subir701/TrustAnchor/blob/main/TrustAnchor.PNG)
---

# 🚀 Key Features

### 🔒 100% Local & Private
All inference runs locally using **Ollama**, ensuring no data leaves your machine.

### ⚡ Tiered Caching Architecture

Two caching layers drastically reduce response latency:

| Layer | Type | Purpose | Speed |
|------|------|------|------|
| **L1 Cache** | Exact Match | SHA-256 hash lookup | Microseconds |
| **L2 Cache** | Semantic Match | Vector similarity search | Milliseconds |

### 🧠 Local LLM Integration

Powered by:

- **Llama 3.2**
- **Nomic Embed Text**

### 🔎 Semantic Vector Search

Uses **PostgreSQL 16 with pgvector** for document retrieval.

⚠️ **Important:** A standard PostgreSQL installation will **not work** without enabling the `pgvector` extension.

### 💬 React Chat Interface

A modern chat UI with:

- Response status indicators
- Cache hit detection
- Real-time generation feedback

*(Frontend maintained in a separate repository.)*

### 📄 Asynchronous Document Ingestion

Efficient pipeline for:

- PDF parsing
- Recursive character chunking
- Embedding generation

### 💻 Hardware Optimized

Custom timeout and reliability layers designed for **CPU-based inference environments**.

---

# 🏗 Architecture

TrustAnchor follows a **Waterfall Retrieval Strategy** to minimize computational overhead.

```
User Question
      │
      ▼
L1 Cache (Exact Match)
      │
      ├── HIT → Return instantly
      │
      ▼
L2 Cache (Semantic Match)
      │
      ├── HIT → Return cached semantic response
      │
      ▼
RAG Pipeline
      │
      ├─ Retrieve context from PostgreSQL (pgvector)
      ├─ Augment prompt
      └─ Query Llama 3.2 via Ollama
      │
      ▼
Response Generated
      │
      ▼
Auto-Cache Result → L1 + L2
```

This strategy ensures **most repeated or similar questions never reach the LLM**, dramatically improving performance.

---

# 🛠 Tech Stack

## Backend

- **Java 21 (Virtual Threads)**
- **Spring Boot 3.4.2**
- Spring Data JPA
- Spring Data Redis
- Lombok

## AI Orchestration

- **LangChain4j**

## Databases

- **PostgreSQL 16 + pgvector**
- **Redis Stack (Vector Search)**

## Inference

- **Ollama**
  - Llama 3.2
  - Nomic Embed Text

## Frontend

- **React**
- **Vite**
- **Tailwind CSS**

*(Hosted in a separate repository)*

## Dev Tools

- Gradle
- JUnit 5
- Mockito

---

# 📋 Prerequisites

Ensure the following tools are installed before running TrustAnchor:

### Required Software

- **Java 21 SDK**
- **Node.js v18+**
- **npm**
- **PostgreSQL 16**
- **Redis Stack**
- **Ollama**

Install required models:

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

---

# ⚙ Installation

## 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/trustanchor.git
cd trustanchor
```

---

## 2️⃣ Setup PostgreSQL with pgvector

A **standard PostgreSQL installation is not enough**.

You must install and enable the **pgvector extension**.

Create database and enable extension:

```sql
CREATE DATABASE trustanchor;

\c trustanchor

CREATE EXTENSION vector;
```

Verify pgvector installation:

```sql
SELECT * FROM pg_extension WHERE extname = 'vector';
```

PostgreSQL should run on:

```
localhost:5432
```

---

## 3️⃣ Setup Redis Stack

Start Redis:

```bash
redis-server
```

Verify Redis:

```bash
redis-cli ping
```

Expected response:

```
PONG
```

Redis should run on:

```
localhost:6379
```

---

## 4️⃣ Build Backend

From the project root:

```bash
./gradlew clean build -x test
```

---

## 5️⃣ Run the Backend

```bash
java -Xms256m -Xmx512m -jar build/libs/TrustAnchor-0.0.1-SNAPSHOT.jar
```

---

# 🖥 Frontend

The frontend application is maintained in a **separate repository**.

It was generated and iteratively developed using **Lovable AI**, which allowed rapid UI prototyping and integration with the TrustAnchor backend.

Frontend repository:

```
https://github.com/subir701/trust-anchor-chat.git
```

To run the frontend:

```bash
git clone https://github.com/subir701/trust-anchor-chat.git
cd frontend
npm install
npm run dev
```

The UI will run on:

```
http://localhost:5173
```

---

# 💬 Usage

1. Start **PostgreSQL**
2. Start **Redis**
3. Start **Ollama**
4. Run the **TrustAnchor backend**
5. Start the **frontend**

Then ask questions about your ingested documents.

Example:

```
What are the key security principles mentioned in the document?
```

Execution flow:

1. Check **L1 Cache**
2. Check **L2 Cache**
3. Run **RAG retrieval if cache miss**
4. Store result in cache

---

# 🧪 Testing

TrustAnchor uses **Mockito** to simulate AI responses.

This allows testing without requiring:

- GPU
- Active LLM
- Running Ollama instance

Run tests:

```bash
./gradlew test
```

---

# ⚙ Configuration

Configuration file:

```
src/main/resources/application.properties
```

Typical parameters:

- Redis configuration
- PostgreSQL connection
- Semantic similarity threshold
- LLM timeouts
- Document chunk size

Example:

```yaml
semantic_similarity_threshold: 0.95
```

---

# 🧩 Examples

### Cache Hit Example

```
User: What is zero trust architecture?

L1 Cache: MISS
L2 Cache: HIT
Response: Returned from semantic cache
Latency: ~5 ms
```

---

### Full RAG Flow

```
User: Explain distributed consensus in the document

L1 Cache: MISS
L2 Cache: MISS
Vector DB Retrieval → LLM Generation
Latency: ~2–5 seconds
```

The response is then automatically cached for future queries.

---

# 🛠 Troubleshooting

### pgvector Not Enabled

Error example:

```
type "vector" does not exist
```

Solution:

```sql
CREATE EXTENSION vector;
```

---

### Redis Not Running

Check Redis:

```bash
redis-cli ping
```

Expected:

```
PONG
```

---

### Ollama Not Running

Check models:

```bash
ollama list
```

Install models if missing:

```bash
ollama pull llama3.2
ollama pull nomic-embed-text
```

---

# 👥 Contributors

Contributions are welcome.

Steps:

1. Fork the repository
2. Create a feature branch
3. Submit a pull request

---

# 📄 License

This project is licensed under the **MIT License**.
