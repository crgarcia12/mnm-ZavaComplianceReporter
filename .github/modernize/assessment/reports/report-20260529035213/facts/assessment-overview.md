# Assessment Overview

This directory contains supplementary architecture and design documents generated as part of the application assessment for **ZavaComplianceReporter**. These documents provide detailed context about the application's structure, data flows, configuration, and business logic to support cloud migration planning.

## Supplementary Documents

| Document | Description |
|---|---|
| [Architecture Diagram](architecture-diagram.md) | Two-layer visualization of the application architecture: high-level component layers (Client, Servlet, Data, External) and detailed internal component relationships with a full component inventory. |
| [Dependency Map](dependency-map.md) | Visual map of all external library dependencies (Servlet API, JSTL, SQL Server JDBC driver) grouped by category, with version/compatibility risk analysis and a note on the absence of test dependencies. |
| [API & Service Contracts](api-service-contracts.md) | Inventory of all six HTTP endpoints, authentication/authorization posture, communication patterns (synchronous JDBC + outbound SSO gateway call), and a sequence diagram of the primary request flow. |
| [Data Architecture](data-architecture.md) | Database configuration (SQL Server, raw JDBC, no ORM), entity model ER diagram for the six inferred database tables, key JDBC query patterns, caching strategy (none), and a data classification assessment identifying PCI-adjacent and PII fields with no encryption-at-rest. |
| [Configuration Inventory](configuration-inventory.md) | Full inventory of all configuration sources (`compliance.properties`, `web.xml`, `Dockerfile`), all seven externalized properties with their environment-variable overrides, secrets posture (hardcoded DB password in source), and framework/runtime version matrix. |
| [Business Workflows](business-workflows.md) | End-to-end documentation of the four core business workflows (SSO authentication, CTR generation, SAR generation, report download), domain entity descriptions, business rules, decision logic, and a sequence diagram of the compliance report generation flow. |
