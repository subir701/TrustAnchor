TrustAnchor v1.0 ⚓
------------------

**TrustAnchor** is a high-performance, private Retrieval-Augmented Generation (RAG) orchestrator built with **Java 21** and **Spring Boot**. It allows you to chat with your local documents (PDFs) with 100% privacy, running entirely on your local machine using **Ollama**.

What makes TrustAnchor unique is its **Tiered Caching Architecture**, specifically engineered to provide near-instant responses on resource-constrained hardware (e.g., 8GB RAM) by minimizing expensive LLM and Vector DB operations.

### 🚀 Key Features

*   **Local LLM Integration:** Powered by **Ollama** (Llama 3.2 & Nomic-Embed-Text).
    
*   **Vector Search:** Utilizes **PostgreSQL** with the pgvector extension for semantic document retrieval.
    
*   **Dual-Tier Redis Caching:**
    
    *   **L1 (Exact Match):** Lightning-fast SHA-256 string hash lookup (Microseconds).
        
    *   **L2 (Semantic Match):** Vector similarity search via **Redis Stack** to catch rephrased questions (Milliseconds).
        
*   **Asynchronous Ingestion:** Efficient PDF parsing and recursive character chunking.
    
*   **Hardware Optimized:** Custom timeout and reliability layers designed for local CPU/RAM bottlenecks.
    

### 🏗 Architecture

TrustAnchor follows a "Waterfall" retrieval strategy to maximize speed and minimize CPU load:

1.  **L1 Cache Check:** Hits Redis for an exact string match.
    
2.  **L2 Cache Check:** Hits Redis Stack for a 95% semantic similarity match.
    
3.  **RAG Flow:** If cache misses, it retrieves context from **Postgres**, augments the prompt, and queries **Llama 3.2**.
    
4.  **Auto-Populate:** New answers are automatically cached back to L1 and L2.
    

### 🛠 Tech Stack

*   **Backend:** Java 21, Spring Boot 3.4.2, Spring Data JPA, Spring Data Redis
    
*   **AI Orchestration:** LangChain4j
    
*   **Databases:** PostgreSQL 16 (pgvector), Redis Stack
    
*   **Inference:** Ollama (Llama 3.2, Nomic-Embed-Text)
    
*   **Tools:** Docker Compose, Lombok, JUnit 5, Mockito
    

### 📋 Prerequisites

*   **Docker & Docker Compose**
    
*   **Java 21 SDK**
    
*   Bashollama pull llama3.2ollama pull nomic-embed-text
    

### 🚦 Quick Start

1.  Bashgit clone https://github.com/yourusername/TrustAnchor.gitcd TrustAnchor
    
2.  Bashdocker-compose up -d_Note: This starts Postgres with pgvector and redis-stack-server._
    
3.  Bash./gradlew bootRun
    
4.  Bashcurl -X POST -F "file=@your\_resume.pdf" http://localhost:8080/trustanchor/upload
    
5.  Bashcurl -i -X POST http://localhost:8080/trustanchor/querys/ask \\-H "Content-Type: application/json" \\-d '{"message": "What are the candidate's core technical skills?"}'
    

### 🧪 Running Tests

TrustAnchor uses Mockito to simulate AI responses, ensuring the caching logic can be verified without requiring a GPU or active LLM:

Bash

Plain textANTLR4BashCC#CSSCoffeeScriptCMakeDartDjangoDockerEJSErlangGitGoGraphQLGroovyHTMLJavaJavaScriptJSONJSXKotlinLaTeXLessLuaMakefileMarkdownMATLABMarkupObjective-CPerlPHPPowerShell.propertiesProtocol BuffersPythonRRubySass (Sass)Sass (Scss)SchemeSQLShellSwiftSVGTSXTypeScriptWebAssemblyYAMLXML`   ./gradlew test   `

### 📈 Roadmap (v2.0)

*   \[ \] Conversational Chat Memory (Stateful sessions).
    
*   \[ \] Metadata-based Source Citations (linking answers to specific PDF pages).
    
*   \[ \] React Frontend Integration (using Lovable AI).
    
*   \[ \] Query Expansion (HyDE) for better vague-query handling.
