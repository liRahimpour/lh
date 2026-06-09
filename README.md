# Legacy Knowledge Hub

Legacy Knowledge Hub is a Java/Spring-based platform for preserving and structuring technical knowledge from legacy software systems.

The goal is to import legacy codebases, analyze their structure, store technical metadata, and provide a foundation for documentation, onboarding, code exploration, and future AI-assisted modernization.

> This project is currently under active development.

---

## Problem

Many organizations still depend on old but business-critical software systems.

Typical challenges are:

- missing or outdated documentation
- knowledge concentrated in a few senior developers
- difficult onboarding for new team members
- unclear dependencies inside old codebases
- risky migration and modernization projects

Legacy Knowledge Hub aims to make this technical knowledge visible, searchable, and reusable.

---

## Current MVP Scope

The current MVP focuses on importing source files, storing metadata, and extracting basic code symbols.

Implemented features:

- project management
- source file metadata management
- single source file upload
- ZIP archive upload and extraction
- automatic file language detection
- SHA-256 file hashing
- MinIO-based source file storage
- SQL symbol extraction
- code symbol persistence
- symbol analysis API
- GitHub Actions backend CI

Currently in development:

- Delphi analyzer
- C# analyzer
- code relationship extraction
- Neo4j integration

Planned features:

- Git import with JGit
- SVN import
- vector search with Qdrant
- AI assistant using RAG
- automatic technical documentation generation
- modernization and transformation reports

---

## Target Technologies

Initial focus:

- Delphi
- C#
- .NET Framework
- SQL
- configuration files

Possible future support:

- COBOL
- VB6
- Java
- mainframe-related technologies
- other legacy enterprise systems

---

## Architecture

The application is currently designed as a **modular monolith**.

This keeps the MVP simple while still allowing clear module boundaries and future growth.

The backend follows a simplified hexagonal architecture:

```text
module/
├── domain/
├── application/
├── ports/
└── infrastructure/
```

Main modules:

```text
backend/src/main/java/com/rahimpour/legacyhub/
├── project/
├── sourcefile/
├── storage/
├── upload/
├── analysis/
├── codesymbol/
├── knowledgegraph/
├── vectorsearch/
├── assistant/
├── documentation/
└── shared/
```

---

## Technology Stack

Backend:

- Java 21
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Boot Actuator
- Maven
- Flyway
- PostgreSQL
- MinIO Java SDK
- Springdoc OpenAPI

Infrastructure:

- Docker
- Docker Compose
- PostgreSQL
- MinIO

Planned infrastructure:

- Neo4j
- Qdrant

---

## Storage Concept

The application separates metadata from actual source files.

- PostgreSQL stores structured metadata such as projects, source files, and extracted code symbols.
- MinIO stores the actual uploaded and extracted source files.
- Neo4j is planned for relationships between code elements.
- Qdrant is planned for semantic code search and AI/RAG use cases.

This separation keeps the system scalable and easier to evolve.

---

## Implemented APIs

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

### Upload API

```http
POST /api/projects/{projectId}/uploads/source-file
POST /api/projects/{projectId}/uploads/source-archive
```

### Code Symbol API

```http
GET /api/projects/{projectId}/symbols
GET /api/projects/{projectId}/symbols/{symbolId}
```

### Analysis API

```http
POST /api/projects/{projectId}/analysis/symbols
```

---

## Development Status

This project is still in an early MVP phase.

The current focus is on building a clean technical foundation before adding advanced AI and knowledge graph features.

Next development steps:

1. Complete basic Delphi analysis
2. Complete basic C# analysis
3. Extract relationships between code symbols
4. Add Neo4j integration
5. Add Git import support
6. Add semantic search with Qdrant
7. Add AI-assisted documentation and code chat

---

## Documentation

Detailed documentation will be added under `/docs`.

Planned documentation structure:

```text
docs/
├── 00-overview.md
├── 01-architecture.md
├── 02-modules.md
├── 03-api.md
├── 04-data-model.md
├── 05-runtime-flows.md
├── 06-deployment.md
├── 07-security.md
├── 08-development-guide.md
├── 09-roadmap.md
├── 10-glossary.md
└── adr/
```

---

## Project Goal

Legacy Knowledge Hub is not just a code upload tool.

The long-term goal is to create a technical knowledge platform that helps teams understand, document, maintain, and modernize legacy software systems with less risk.
