# mnm-ZavaComplianceReporter

## Summary

| Metric | Value |
|--------|-------|
| Total Issues | 8 |
| Mandatory Blockers | 5 |
| Potential Issues | 2 |

## Component Information

| Property | Value |
|----------|-------|
| Language | Java |
| Frameworks | Servlet 3.1, JSP, JSTL 1.2 |
| Build tools | Gradle |
| JDK version | 8 |
| Description | Bank compliance reporting application for CTR and SAR filings to FinCEN |
| Application type | Web Application (WAR) |
| Lines of code | 597 |

## Cloud Readiness Issues

| Issue Name | Criticality | Story Points | Occurrences |
|------------|-------------|--------------|-------------|
| HttpSession state requires sticky sessions in cloud deployments | Mandatory | 5 | [3](#HttpSession_state_requires_sticky_sessions_in_cloud_deployments) |
| Database credentials hardcoded in committed properties file | Mandatory | 1 | [2](#Database_credentials_hardcoded_in_committed_properties_file) |
| JDBC DriverManager used without a connection pool | Potential | 3 | [1](#JDBC_DriverManager_used_without_a_connection_pool) |
| java.net.HttpURLConnection used for external service calls without retry or circuit breaker | Potential | 3 | [1](#java_net_HttpURLConnection_used_for_external_service_calls_without_retry_or_circuit_breaker) |
| WAR-based deployment requires Tomcat servlet container | Optional | 5 | [1](#WAR-based_deployment_requires_Tomcat_servlet_container) |

### Issue Details

<details id="HttpSession_state_requires_sticky_sessions_in_cloud_deployments">
<summary><b>HttpSession state requires sticky sessions in cloud deployments</b> — affected files</summary>

- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 26)`
- `src/main/java/com/zavabank/compliancereporter/LoginServlet.java (line 29)`
- `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java (line 22)`

</details>

<details id="Database_credentials_hardcoded_in_committed_properties_file">
<summary><b>Database credentials hardcoded in committed properties file</b> — affected files</summary>

- `src/main/resources/compliance.properties (line 4)`
- `src/main/resources/compliance.properties (line 5)`

</details>

<details id="JDBC_DriverManager_used_without_a_connection_pool">
<summary><b>JDBC DriverManager used without a connection pool</b> — affected files</summary>

- `src/main/java/com/zavabank/compliancereporter/ComplianceConnectionFactory.java (line 15)`

</details>

<details id="java_net_HttpURLConnection_used_for_external_service_calls_without_retry_or_circuit_breaker">
<summary><b>java.net.HttpURLConnection used for external service calls without retry or circuit breaker</b> — affected files</summary>

- `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java (line 76)`

</details>

<details id="WAR-based_deployment_requires_Tomcat_servlet_container">
<summary><b>WAR-based deployment requires Tomcat servlet container</b> — affected files</summary>

- `build.gradle (line 5)`

</details>

## Upgrade Issues

| Issue Name | Criticality | Story Points | Occurrences |
|------------|-------------|--------------|-------------|
| javax.servlet API replaced by jakarta.servlet | Mandatory | 3 | [26](#javax_servlet_API_replaced_by_jakarta_servlet) |
| JSTL 1.2 (javax namespace) replaced by JSTL 3.0 (jakarta namespace) | Mandatory | 1 | [2](#JSTL_1_2_javax_namespace_replaced_by_JSTL_3_0_jakarta_namespace) |
| Application targets Java 8 (end of standard support) | Mandatory | 5 | [2](#Application_targets_Java_8_end_of_standard_support) |

### Issue Details

<details id="javax_servlet_API_replaced_by_jakarta_servlet">
<summary><b>javax.servlet API replaced by jakarta.servlet</b> — affected files</summary>

- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 4)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 5)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 6)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 7)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 8)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 9)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 10)`
- `src/main/java/com/zavabank/compliancereporter/AuthFilter.java (line 11)`
- `src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java (line 7)`
- `src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java (line 8)`
- `src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java (line 9)`
- `src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java (line 7)`
- `src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java (line 8)`
- `src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java (line 9)`
- `src/main/java/com/zavabank/compliancereporter/HealthServlet.java (line 4)`
- `src/main/java/com/zavabank/compliancereporter/HealthServlet.java (line 5)`
- `src/main/java/com/zavabank/compliancereporter/HealthServlet.java (line 6)`
- `src/main/java/com/zavabank/compliancereporter/LoginServlet.java (line 4)`
- `src/main/java/com/zavabank/compliancereporter/LoginServlet.java (line 5)`
- `src/main/java/com/zavabank/compliancereporter/LoginServlet.java (line 6)`
- `src/main/java/com/zavabank/compliancereporter/LoginServlet.java (line 7)`
- `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java (line 7)`
- `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java (line 8)`
- `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java (line 9)`
- `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java (line 10)`
- `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java (line 10)`

</details>

<details id="JSTL_1_2_javax_namespace_replaced_by_JSTL_3_0_jakarta_namespace">
<summary><b>JSTL 1.2 (javax namespace) replaced by JSTL 3.0 (jakarta namespace)</b> — affected files</summary>

- `build.gradle (line 17)`
- `src/main/webapp/WEB-INF/jsp/reports.jsp (line 2)`

</details>

<details id="Application_targets_Java_8_end_of_standard_support">
<summary><b>Application targets Java 8 (end of standard support)</b> — affected files</summary>

- `build.gradle (line 10)`
- `build.gradle (line 11)`

</details>

---

## Codebase Insights

> **Note:** These documents are generated by AI and may contain inaccuracies or incomplete information. Please review carefully.

1. **[Architecture Diagram](facts/architecture-diagram.md)** — Understand the big picture: system layers and component relationships
2. **[Dependency Map](facts/dependency-map.md)** — Know what the project depends on and where the risks are
3. **[API & Service Contracts](facts/api-service-contracts.md)** — See how services communicate and what contracts they expose
4. **[Data Architecture](facts/data-architecture.md)** — Explore data models, storage, and data flow patterns
5. **[Configuration Inventory](facts/configuration-inventory.md)** — Review how the application is configured across environments
6. **[Business Workflows](facts/business-workflows.md)** — Trace end-to-end business processes and domain logic

[Share feedback](https://aka.ms/ghcp-appmod/feedback)
