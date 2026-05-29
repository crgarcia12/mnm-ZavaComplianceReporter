# Assessment Overview

This directory contains supplementary analysis documents generated alongside the core migration assessment for **ZavaComplianceReporter** — a Java 8 bank compliance reporting application. These documents provide architectural context that enriches the migration findings in `report.json`.

## Supplementary Documents

1. **[Architecture Diagram](architecture-diagram.md)** — Understand the big picture: system layers and component relationships  
   Application-layer and component-relationship diagrams showing how Servlets, Filters, domain classes, SQL Server, and the ZavaBank Auth Gateway are connected.

2. **[Dependency Map](dependency-map.md)** — Know what the project depends on and where the risks are  
   Visual map of all declared external dependencies (servlet API, JSTL, SQL Server JDBC driver) grouped by functional category, with version compatibility risk analysis.

3. **[API & Service Contracts](api-service-contracts.md)** — See how services communicate and what contracts they expose  
   Inventory of all 6 HTTP endpoints, SSO authentication flow, communication patterns with the ZavaBank Auth Gateway, and the security posture of each endpoint.

4. **[Data Architecture](data-architecture.md)** — Explore data models, storage, and data flow patterns  
   Inferred ER diagram of the 6 SQL Server tables (`ComplianceReports`, `Transactions`, `Accounts`, `FraudAlerts`, `SessionTokens`, `Users`), raw JDBC query inventory, and data classification findings (PII, PCI, credentials).

5. **[Configuration Inventory](configuration-inventory.md)** — Review how the application is configured across environments  
   Full inventory of the 7 configuration properties in `compliance.properties`, environment variable overrides, framework versions, and secrets provisioning assessment.

6. **[Business Workflows](business-workflows.md)** — Trace end-to-end business processes and domain logic  
   End-to-end documentation of the CTR and SAR generation workflows, SSO authentication decision chain, SAR alert inclusion criteria, and authorization model.
