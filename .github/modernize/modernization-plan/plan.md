# Modernization Plan: ZavaComplianceReporter Azure modernization

**Project**: mnm-ZavaComplianceReporter

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Java Servlet 3.1, JSP, JSTL
- **Build Tool**: Gradle
- **Database**: Microsoft SQL Server
- **Key Dependencies**: javax.servlet-api 3.1.0, JSTL 1.2, SQL Server JDBC 12.8.1.jre8, Tomcat via Docker

---

## Overview

> This migration moves ZavaComplianceReporter to Azure while preserving the
> current compliance reporting workflow. The application currently runs as a
> Java Servlet/JSP web application packaged as a WAR, connects to SQL Server,
> and includes configuration and security findings that should be addressed
> before cloud deployment. The new architecture will:
>
> - Deploy the existing web application to Azure Container Apps
> - Keep infrastructure provisioning out of scope and use an existing Azure environment
> - Remediate dependency and application security findings before deployment
>
> The migration follows a phased approach that first prepares the application
> for secure cloud deployment and then deploys it to Azure.

---

## Migration Impact Summary

| Application | Original Service | New Azure Service | Authentication | Comments |
|-------------|------------------|-------------------|----------------|----------|
| ZavaComplianceReporter | Tomcat-hosted WAR | Azure Container Apps | Managed Identity | Use the existing Dockerfile; no separate infra task |

---

## Open Questions & Questionnaire

- [x] Q: Should the plan include environment/infrastructure provisioning? → A: No — focus on code migration and deployment to an existing Azure environment.
- [x] Q: Should the plan include integration testing to verify migrated services? → A: No — integration testing was not explicitly requested, so no integration test task was added.
- [x] Q: Should the plan include a security scan and CVE remediation task? → A: Yes — include security and dependency remediation before deployment.
- [x] Q: Which Azure deployment target should the plan use? → A: Azure Container Apps.
- [x] Q: Should the plan include containerization? → A: Not as a separate task — Azure Container Apps deployment covers containerization and the repository already includes `/Dockerfile`.
