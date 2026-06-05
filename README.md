# Legacy Knowledge Hub

Legacy Knowledge Hub is a Java/Spring-based platform for preserving technical knowledge from legacy software systems.

The goal of this project is to analyze old codebases, extract structural knowledge, store metadata and relationships, and provide AI-assisted documentation, chat, onboarding support, and transformation guidance.

This project is currently under active development.

---

## Problem Statement

Many organizations still depend on old but business-critical software systems.

Typical problems are:

- Legacy systems are poorly documented
- Critical knowledge exists only in the heads of senior developers
- Developers are retiring or leaving the company
- Codebases are hard to understand and difficult to modernize
- Migration and transformation projects are risky because system knowledge is incomplete

This project aims to help preserve and structure that knowledge before it is lost.

---

## Vision

When finished, Legacy Knowledge Hub should allow users to import legacy source code and automatically build a technical knowledge base around it.

The planned system should support:

- Importing source code from ZIP uploads
- Importing code from Git repositories
- Importing code from SVN repositories
- Detecting source files and technologies
- Analyzing legacy code structures
- Extracting classes, methods, forms, procedures, SQL statements, and dependencies
- Storing code metadata in PostgreSQL
- Storing source files in object storage such as MinIO
- Storing code relationships in Neo4j
- Storing semantic code chunks in Qdrant
- Providing an AI assistant using RAG (chat with Code)
- Generating technical documentation
- Supporting onboarding for new developers
- Supporting transformation and modernization planning

---

## Target Legacy Technologies

The first focus is on legacy enterprise systems, especially:

- Delphi
- C#
- .NET Framework
- SQL
- Configuration files

Later, the system may support:

- COBOL
- VB6
- Java
- Mainframe-related technologies
- Other legacy languages and platforms

The MVP focuses on a small and realistic scope first.

---

## Current Features

The project currently supports:

- Spring Boot backend
- PostgreSQL integration
- Flyway database migrations
- Docker Compose infrastructure
- Project management API
- Source file metadata API
- GitHub Actions backend CI

Implemented APIs:

### Project API

```http
POST /api/projects
GET  /api/projects
GET  /api/projects/{projectId}
```

### Source File Metadata API

```http
POST /api/projects/{projectId}/source-files
GET  /api/projects/{projectId}/source-files
GET  /api/projects/{projectId}/source-files/{sourceFileId}
```

---

## Planned Features

The next development steps are:

- MinIO integration for file storage
- Upload API for real source files
- Automatic source file metadata extraction
- ZIP upload support
- Git import support with JGit
- SVN import support
- Basic C# code analysis
- Basic Delphi code analysis
- SQL analysis
- Neo4j integration for code relationships
- Qdrant integration for vector search
- AI/RAG assistant for code questions
- Markdown documentation generation
- Architecture and dependency diagrams
- Legacy risk and transformation reports

---

## Architecture Overview

The project follows a **modular monolith architecture**.

At this stage, microservices are intentionally avoided. The goal is to keep the MVP simple, maintainable, and easy to evolve.

The backend is structured around business modules instead of technical layers.

Example:

```text
backend/
└── src/main/java/com/lipad/legacyhub/
    ├── project/
    ├── sourcefile/
    ├── storage/
    ├── upload/
    ├── analysis/
    ├── knowledgegraph/
    ├── vectorsearch/
    ├── assistant/
    ├── documentation/
    └── shared/
```

Each module follows a simplified hexagonal architecture:

```text
module/
├── domain/
├── application/
├── ports/
└── infrastructure/
```

---

## Module Responsibilities

### Project Module

The project module manages legacy software projects.

Responsibilities:

- Create projects
- Retrieve projects
- Track project status
- Provide the root entity for imports, source files, analysis jobs, and documentation

Example entity:

```text
Project
```

---

### Source File Module

The source file module manages metadata about source files.

Responsibilities:

- Store file metadata
- Link files to projects
- Store file path, extension, language, hash, and storage key
- Provide APIs to retrieve source files for a project

> **Important:** The actual file content is not stored in PostgreSQL. PostgreSQL stores only metadata.

Example entity:

```text
SourceFile
```

---

### Storage Module

The storage module is planned for storing real files.

Responsibilities:

- Store uploaded files
- Store original ZIP archives
- Store extracted source files
- Store generated documentation
- Store export files

Planned technology:

- MinIO

Later, the storage backend could be replaced by:

- Amazon S3
- Azure Blob Storage
- Any S3-compatible object storage

---

### Upload Module

The upload module is planned for handling user uploads.

Responsibilities:

- Accept source file uploads
- Accept ZIP uploads
- Validate uploaded files
- Calculate file hashes
- Store files in object storage
- Create corresponding source file metadata records

---

### Analysis Module

The analysis module is planned for code analysis.

Responsibilities:

- Analyze C# source code
- Analyze Delphi source code
- Analyze SQL files
- Extract classes, methods, procedures, forms, events, database access, and dependencies

Planned analyzers:

- C# analyzer based on Roslyn
- Delphi structure parser
- SQL parser
- Config file analyzer

---

### Knowledge Graph Module

The knowledge graph module is planned for storing relationships between code elements.

Example relationships:

```text
CustomerForm
  -> triggers
BtnSaveClick

BtnSaveClick
  -> calls
SaveCustomer

SaveCustomer
  -> writes
CUSTOMER_TABLE
```

Planned technology:

- Neo4j

---

### Vector Search Module

The vector search module is planned for semantic code search.

Responsibilities:

- Split source code into meaningful chunks
- Generate embeddings for code chunks
- Store embeddings
- Search code by meaning, not only by exact words

Planned technology:

- Qdrant

---

### Assistant Module

The assistant module is planned for AI-based interaction with the codebase.

Responsibilities:

- Answer questions about legacy code
- Explain code behavior
- Support developer onboarding
- Use retrieved project knowledge instead of guessing
- Provide source-based answers

Planned approach:

- RAG - Retrieval Augmented Generation

---

### Documentation Module

The documentation module is planned for generating technical documentation.

Responsibilities:

- Generate architecture overviews
- Generate module descriptions
- Generate onboarding guides
- Generate Markdown documentation
- Generate Mermaid diagrams
- Later export to PDF or DOCX

---

## Why Modular Monolith?

A modular monolith is used because the project is still in MVP phase.

Advantages:

- Simpler deployment
- Easier debugging
- Less infrastructure overhead
- Clear module boundaries
- Easier transition to microservices later if necessary

Starting directly with microservices would add unnecessary complexity too early.

---

## Why Hexagonal Architecture?

Hexagonal architecture helps keep business logic independent from external technologies.

The application should not directly depend on:

- PostgreSQL
- MinIO
- Git
- SVN
- Neo4j
- Qdrant
- LLM providers

Instead, the application depends on ports/interfaces.

Example:

```text
FileStoragePort
├── LocalFileStorageAdapter
├── MinioFileStorageAdapter
└── S3FileStorageAdapter
```

This makes it easier to replace infrastructure later.

---

## Technology Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Maven
- Flyway
- PostgreSQL

### Infrastructure

- Docker
- Docker Compose
- PostgreSQL
- MinIO
- Neo4j
- Qdrant

### Planned AI and Knowledge Components

- LangChain4j
- Qdrant
- Neo4j
- OpenAI
- Azure OpenAI
- Local LLMs

### Planned Frontend

- React
- TypeScript
- Tailwind CSS
- Mermaid.js

---

## Infrastructure Components

### PostgreSQL

PostgreSQL stores structured application data:

- Projects
- Source file metadata
- Import jobs
- Analysis jobs
- Status information

PostgreSQL does **not** store large source files directly.

---

### MinIO

MinIO is planned as object storage for real files:

- Uploaded ZIP files
- Source files
- Generated documentation
- Export files
- Analysis artifacts

PostgreSQL stores only the `storageKey` pointing to the file in MinIO.

Example:

```text
projects/{projectId}/source/src/forms/CustomerForm.pas
```

---

### Neo4j

Neo4j is planned for code relationships.

Example:

```text
CustomerForm -> triggers -> BtnSaveClick
BtnSaveClick -> calls -> SaveCustomer
SaveCustomer -> writes -> CUSTOMER_TABLE
```

---

### Qdrant

Qdrant is planned for semantic search.

It will store code chunks as vector embeddings so that users can search by meaning, not only by exact words.

Example:

A user asks:

```text
Where is the customer saved?
```

The system may find methods such as:

```text
SaveCustomer()
PersistClient()
UpdatePartner()
```

even if the exact search term does not appear in the code.

---

## AI and RAG Concept

The planned AI assistant will use **RAG**.

RAG means:

```text
Retrieval Augmented Generation
```

Instead of asking an AI model to guess, the system first retrieves relevant project information.

Planned flow:

```text
User question
→ create question embedding
→ search similar code chunks in Qdrant
→ query code relationships from Neo4j
→ build context
→ send context to LLM
→ return answer with sources
```

The assistant should answer based on project sources and should say when the available information is not sufficient.

---

## Prerequisites

You need the following tools installed:

- Java 21
- Maven
- Docker
- Docker Compose
- Git

Optional but useful:

- Postman
- DBeaver
- IntelliJ IDEA
- GitHub Desktop or command line Git

---

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/liRahimpour/lh.git
cd lh
```

### 2. Start the infrastructure

```bash
cd infrastructure
docker compose up -d
```

This starts the local infrastructure services.

Expected services:

- PostgreSQL
- MinIO
- Neo4j
- Qdrant

### 3. Start the backend

```bash
cd ../backend
mvn spring-boot:run
```

The backend should be available at:

```text
http://localhost:8081
```

Depending on your local configuration, the port may be `8080` instead of `8081`.

---

## Local Service URLs

### Backend

```text
http://localhost:8081
```

Ping endpoint:

```http
GET /api/ping
```

### PostgreSQL

```text
Host:     localhost
Port:     5432
Database: legacyhub
User:     legacyhub
Password: legacyhub
```

### MinIO

```text
API:     http://localhost:9000
Console: http://localhost:9001
```

### Neo4j

```text
Browser: http://localhost:7474
Bolt:    bolt://localhost:7687
```

### Qdrant

```text
http://localhost:6333
```

---

## API Usage

### Create a Project

```http
POST /api/projects
Content-Type: application/json
```

Request body:

```json
{
  "name": "Legacy System",
  "description": "Legacy Delphi/C# system for knowledge preservation",
  "technologyHint": "DELPHI_CSHARP_SQL"
}
```

---

### Get All Projects

```http
GET /api/projects
```

---

### Get Project by ID

```http
GET /api/projects/{projectId}
```

---

## Source File Metadata API

### Create Source File Metadata

```http
POST /api/projects/{projectId}/source-files
Content-Type: application/json
```

Request body:

```json
{
  "path": "src/forms/SingleCustomerForm.pas",
  "filename": "SingleCustomerForm.pas",
  "extension": "pas",
  "language": "DELPHI",
  "sizeBytes": 34211,
  "contentHash": "abc123",
  "storageKey": "projects/{projectId}/source/src/forms/SingleCustomerForm.pas"
}
```

---

### Get All Source Files for a Project

```http
GET /api/projects/{projectId}/source-files
```

---

### Get Source File by ID

```http
GET /api/projects/{projectId}/source-files/{sourceFileId}
```

---

## Example Postman Setup

Recommended Postman environment variable:

```text
localhost = http://localhost:8081
```

Example request:

```text
{{localhost}}/api/projects
```

---

## Database Migrations

Flyway is used for database migrations.

Current migrations:

```text
V1__create_projects_table.sql
V2__create_source_files_table.sql
```

Migrations are located in:

```text
backend/src/main/resources/db/migration/
```

Hibernate should validate the schema, not create it automatically.

Recommended setting:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

---

## Development Workflow

Recommended development order:

1. Build a small vertical slice.
2. Test with Postman.
3. Verify data in PostgreSQL.
4. Commit changes.
5. Let GitHub Actions run.
6. Continue with the next small module.

> **Recommendation:** Avoid building AI features before the import, storage, and metadata foundation is stable.

---

## GitHub Actions

The project uses GitHub Actions for backend CI.

The CI should verify that the backend builds successfully.

Typical steps:

1. Checkout repository.
2. Set up Java 21.
3. Build backend with Maven.
4. Run tests.

---

## Roadmap

### Phase 1: Foundation

- Spring Boot backend
- PostgreSQL
- Flyway
- Project API
- Source file metadata API
- Docker Compose infrastructure

### Phase 2: Storage and Upload

- MinIO integration
- Single source file upload
- Automatic metadata extraction
- File hashing
- Storage key generation

### Phase 3: Import

- ZIP upload
- ZIP extraction
- File inventory generation
- Git import
- SVN import

### Phase 4: Analysis

- C# analyzer
- Delphi parser
- SQL parser
- Config parser
- Code symbols
- Code relations

### Phase 5: Knowledge Graph

- Neo4j integration
- Store code entities
- Store relationships
- Query dependency chains
- Generate diagrams

### Phase 6: Vector Search

- Chunk source code
- Generate embeddings
- Store embeddings in Qdrant
- Semantic code search

### Phase 7: AI Assistant

- RAG pipeline
- Source-based answers
- Code explanation
- Onboarding questions
- Confidence handling

### Phase 8: Documentation

- Markdown documentation generation
- Mermaid diagrams
- System overview
- Module descriptions
- Onboarding guide

### Phase 9: Transformation Support

- Legacy risk analysis
- Dependency risk reports
- Knowledge loss risk reports
- Modernization suggestions
- Migration planning support

---

## Important Design Decisions

### Files are not stored in PostgreSQL

PostgreSQL stores metadata only.

Actual files should be stored in object storage such as MinIO.

---

### MinIO is used for local object storage

MinIO is S3-compatible. This makes it easier to migrate later to Amazon S3 or another compatible object storage.

---

### The backend should remain stateless

This helps later deployment to Docker, Kubernetes, or cloud environments.

---

### Analysis should run as jobs

Long-running analysis tasks should not block HTTP requests.

Planned model:

```http
POST /api/projects/{projectId}/analysis
```

```text
→ creates analysis job
→ background worker processes job
→ UI polls job status
```

---

### AI answers must be source-based

The AI assistant should not freely guess.

It should answer based on retrieved code, metadata, graph relationships, and documentation.

---

## Project Status

This project is experimental and under active development.

It is intended as a learning project, a technical proof of concept, and a possible foundation for a real **Legacy Knowledge Hub** product.
