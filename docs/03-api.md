# Legacy Knowledge Hub – API Reference

This document describes the currently implemented REST API and the local development services of Legacy Knowledge Hub.

> **Status:** MVP / active development. Endpoints and response models can change while the project evolves.

---

## 1. Terminology

### Endpoint

An **endpoint** is a reachable URL of the LegacyHub backend API.

Examples:

```text
GET  http://localhost:8081/api/projects
POST http://localhost:8081/api/projects/{projectId}/analysis/symbols
GET  http://localhost:8081/api/projects/{projectId}/relations
```

The API endpoints are intended for clients such as a future frontend, CLI, Postman, Swagger UI, or external integrations.

### Base URL

The local Spring Boot backend runs on:

```text
http://localhost:8081
```

All current business API endpoints begin with:

```text
http://localhost:8081/api
```

---

## 2. Documentation Sources

Two forms of API documentation are used:

- **Swagger UI** is the live, interactive API documentation generated from the running Spring Boot application.
- This Markdown document explains the purpose of the endpoints, the typical workflow, and the local infrastructure ports.

Swagger UI:

```text
http://localhost:8081/swagger-ui/index.html
```

OpenAPI specification, when Springdoc is enabled:

```text
http://localhost:8081/v3/api-docs
```

---

## 3. Typical API Workflow

A normal analysis flow looks like this:

```text
1. Create a project
2. Upload one source file or a ZIP archive
3. Inspect stored source-file metadata
4. Start symbol analysis
5. Inspect extracted symbols
6. Start relation analysis
7. Inspect relations between symbols
```

Example flow:

```text
POST /api/projects
POST /api/projects/{projectId}/uploads/source-archive
POST /api/projects/{projectId}/analysis/symbols
GET  /api/projects/{projectId}/symbols
POST /api/projects/{projectId}/analysis/relations
GET  /api/projects/{projectId}/relations
```

---

## 4. Project API

Base path:

```text
/api/projects
```

### Create a project

```http
POST /api/projects
Content-Type: application/json
```

Creates a project that groups imported source files, detected symbols, and detected relations.

Example request body:

```json
{
  "name": "Customer Management Legacy System",
  "description": "Imported Java, Delphi and SQL source code",
  "technologyHint": "JAVA"
}
```

Expected response:

```text
201 Created
```

### Get all projects

```http
GET /api/projects
```

Returns all known LegacyHub projects.

Expected response:

```text
200 OK
```

### Get one project

```http
GET /api/projects/{projectId}
```

Returns one project by its UUID.

Expected response:

```text
200 OK
```

---

## 5. Source File Metadata API

Base path:

```text
/api/projects/{projectId}/source-files
```

This API exposes metadata about source files that have been imported into a project. The actual file content is stored in MinIO; PostgreSQL stores metadata such as file name, detected language, hash, and storage key.

### Create source-file metadata

```http
POST /api/projects/{projectId}/source-files
```

Creates a source-file metadata record.

> In the normal application flow, source-file records are usually created by the upload endpoints rather than manually.

### Get all source files of a project

```http
GET /api/projects/{projectId}/source-files
```

Returns all stored source-file metadata records for a project.

### Get one source file

```http
GET /api/projects/{projectId}/source-files/{sourceFileId}
```

Returns metadata for one source file.

---

## 6. Upload API

Base path:

```text
/api/projects/{projectId}/uploads
```

The upload module receives files from the client. It stores source content in MinIO and creates source-file metadata records in PostgreSQL.

### Upload one source file

```http
POST /api/projects/{projectId}/uploads/source-file
Content-Type: multipart/form-data
```

Form field:

```text
file = <source file>
```

Example with `curl`:

```bash
curl -X POST \
  -F "file=@CustomerService.java" \
  http://localhost:8081/api/projects/{projectId}/uploads/source-file
```

Expected response:

```text
201 Created
```

The response contains source-file metadata.

### Upload a source archive

```http
POST /api/projects/{projectId}/uploads/source-archive
Content-Type: multipart/form-data
```

Form field:

```text
file = <ZIP archive>
```

Example with `curl`:

```bash
curl -X POST \
  -F "file=@legacy-source.zip" \
  http://localhost:8081/api/projects/{projectId}/uploads/source-archive
```

Expected response:

```text
201 Created
```

The archive upload process:

```text
1. Receives the ZIP archive
2. Stores the original archive in MinIO
3. Reads ZIP entries
4. Skips ignored or unsupported entries
5. Detects each file language from its extension
6. Calculates file hashes
7. Stores relevant extracted files in MinIO
8. Creates SourceFile metadata records in PostgreSQL
```

---

## 7. Symbol Analysis API

Base path:

```text
/api/projects/{projectId}/analysis
```

Symbol analysis extracts basic code elements from imported source files.

Examples of symbols:

```text
Java:    class, interface, enum, record, method, field
Delphi:  unit, class, form, component, procedure, function
SQL:     table, view, procedure, function
```

### Analyze symbols

```http
POST /api/projects/{projectId}/analysis/symbols
```

This endpoint starts symbol analysis for all source files of a project.

High-level flow:

```text
SourceFile metadata
  -> language-specific analyzer selection
  -> detected symbols
  -> CodeSymbol persistence in PostgreSQL
```

The service selects an analyzer by the language stored on the source file.

Examples:

```text
JAVA        -> JavaSourceFileAnalyzer
DELPHI      -> DelphiSourceFileAnalyzer
SQL         -> SqlSourceFileAnalyzer
```

### Get all symbols of a project

```http
GET /api/projects/{projectId}/symbols
```

Returns all extracted symbols of a project.

### Get one symbol

```http
GET /api/projects/{projectId}/symbols/{symbolId}
```

Returns one extracted symbol.

---

## 8. Relation Analysis API

Base path:

```text
/api/projects/{projectId}/analysis
```

Relation analysis creates relationships between already extracted code symbols.

Example:

```text
CustomerService -> HAS_METHOD -> saveCustomer
CustomerService -> HAS_FIELD  -> customerRepository
```

### Analyze relations

```http
POST /api/projects/{projectId}/analysis/relations
```

This endpoint analyzes project symbols and stores detected code relations.

High-level flow:

```text
CodeSymbols
  -> group symbols by source file
  -> select relation analyzer for the file language
  -> detected relations
  -> CodeRelation persistence in PostgreSQL
```

Current MVP relation analysis focuses on simple structural relations, especially Java container-to-member relations such as:

```text
CLASS -> HAS_METHOD -> METHOD
CLASS -> HAS_FIELD  -> FIELD
```

---

## 9. Code Relation API

Base path:

```text
/api/projects/{projectId}/relations
```

Relations are returned as enriched responses. Besides IDs, the API includes symbol names and types so that clients do not need to manually resolve every symbol ID.

Example meaning:

```text
CustomerService (CLASS)
  -> HAS_METHOD
  -> saveCustomer (METHOD)
```

### Get all relations of a project

```http
GET /api/projects/{projectId}/relations
```

Returns all relations belonging to a project.

Typical response fields:

```text
id
projectId
sourceSymbolId
sourceSymbolName
sourceSymbolType
targetSymbolId
targetSymbolName
targetSymbolType
type
createdAt
```

### Get one relation

```http
GET /api/projects/{projectId}/relations/{relationId}
```

Returns one relation.

Expected responses:

```text
200 OK        relation exists
404 Not Found relation does not exist in this project
```

### Get outgoing relations of a symbol

```http
GET /api/projects/{projectId}/relations/symbols/{symbolId}/outgoing
```

Outgoing means that the requested symbol is the source of a relation.

Example:

```text
CustomerService -> HAS_METHOD -> saveCustomer
```

For `CustomerService`, this is an outgoing relation.

Expected responses:

```text
200 OK        symbol exists; result may be an empty list
404 Not Found symbol does not exist in this project
```

### Get incoming relations of a symbol

```http
GET /api/projects/{projectId}/relations/symbols/{symbolId}/incoming
```

Incoming means that the requested symbol is the target of a relation.

Example:

```text
CustomerService -> HAS_METHOD -> saveCustomer
```

For `saveCustomer`, this is an incoming relation.

Expected responses:

```text
200 OK        symbol exists; result may be an empty list
404 Not Found symbol does not exist in this project
```

---

## 10. Language Detection

During import, `FileLanguageDetector` determines the language or category based on the file extension.

Current mappings:

| File extension | SourceFileLanguage |
|---|---|
| `.cs` | `CSHARP` |
| `.pas` | `DELPHI` |
| `.dfm` | `DELPHI_FORM` |
| `.dpr` | `DELPHI_PROJECT` |
| `.java` | `JAVA` |
| `.sql` | `SQL` |
| `.xml` | `XML` |
| `.json` | `JSON` |
| `.config`, `.properties`, `.yml`, `.yaml` | `CONFIG` |
| other / missing extension | `UNKNOWN` |

The detector normalizes Windows and Unix paths and converts extensions to lowercase. Therefore these are treated equally:

```text
src\\main\\java\\CustomerService.JAVA
src/main/java/CustomerService.java
```

Both are detected as:

```text
JAVA
```

---

## 11. Local Development Services and Ports

The application consists of the Spring Boot backend and supporting infrastructure services.

| Service | Local address | Purpose | Public application API? |
|---|---|---|---|
| LegacyHub backend | `http://localhost:8081` | Spring Boot application and REST API | Yes |
| Swagger UI | `http://localhost:8081/swagger-ui/index.html` | Interactive API documentation and manual endpoint testing | Yes, documentation UI |
| PostgreSQL | `localhost:5432` | Stores structured metadata, symbols, and relations | No |
| MinIO S3 API | `http://localhost:9000` | Object-storage API used by the backend | No |
| MinIO Console | `http://localhost:9001` | Browser UI for inspecting stored objects | No |

### Important distinction

#### Business API endpoints

These are public endpoints of LegacyHub itself:

```text
http://localhost:8081/api/...
```

Examples:

```text
GET  /api/projects
POST /api/projects/{projectId}/uploads/source-archive
POST /api/projects/{projectId}/analysis/symbols
GET  /api/projects/{projectId}/relations
```

#### Infrastructure ports

These are technical service endpoints used by the backend or by developers:

```text
PostgreSQL: 5432
MinIO API: 9000
MinIO Console: 9001
```

They are not part of the LegacyHub business API.

### Planned services

Neo4j and Qdrant are planned for later project phases. They are not listed as active local services until they are added to the Docker Compose setup.

Planned responsibilities:

```text
Neo4j  -> graph representation and graph queries for code relationships
Qdrant -> vector search and semantic retrieval for future AI/RAG features
```

---

## 12. Storage Responsibility

LegacyHub separates metadata from actual source content.

```text
PostgreSQL
  - projects
  - source-file metadata
  - code symbols
  - code relations

MinIO
  - uploaded source files
  - uploaded ZIP archives
  - extracted source-file content
```

This design avoids storing large source-file contents directly in relational tables and keeps file storage independent from metadata storage.

---

## 13. Error Handling – Current MVP Behaviour

The relation endpoints distinguish between these two cases:

```text
1. Requested symbol exists but has no relations
   -> 200 OK with []

2. Requested symbol does not exist in the project
   -> 404 Not Found
```

For a missing individual relation:

```text
GET /api/projects/{projectId}/relations/{relationId}
-> 404 Not Found
```

A project-wide, consistent error-response format is planned for a later cleanup step. The current MVP deliberately keeps error handling focused on the concrete endpoints that require it.

---

## 14. Testing Guidance

The recommended test stack for the current project is:

```text
JUnit 5
AssertJ
Mockito when needed
Spring Boot Test
MockMvc
Testcontainers with PostgreSQL
GitHub Actions
```

Suggested testing layers:

```text
Unit tests
  - FileLanguageDetector
  - Java / Delphi / SQL symbol analyzers
  - Java relation analyzer

API integration tests
  - important happy paths
  - missing relation -> 404
  - missing symbol incoming/outgoing -> 404
  - existing symbol without relations -> 200 []

Persistence integration tests
  - Flyway migrations
  - JPA mappings
  - repository adapters
```

---

## 15. Current Boundaries and Next Steps

Implemented core capabilities:

```text
Project management
Source-file metadata
Single-file upload
ZIP archive upload and extraction
MinIO storage
Language detection
Symbol analysis
Relation analysis
Relation API with enriched responses
```

Next technical priorities:

```text
1. Add automated tests for analyzers and critical APIs
2. Improve analyzer accuracy and add more legacy-language support
3. Add Neo4j graph synchronization
4. Add Git and SVN import
5. Add Qdrant and semantic search
6. Add AI-assisted documentation and code chat
```
