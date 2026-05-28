# Security Assessment Report

**Generated:** 2026-05-28T23:00:00.0000000Z

## Summary

| Metric | Count |
|--------|-------|
| Total Findings | 11 |
| CVE Vulnerabilities | 1 |
| CWE Vulnerabilities | 10 |
| Total Rules Assessed | 59 |
| Rules Passed | 49 |

### By Severity

| Severity | Count |
|----------|-------|
| mandatory | 1 |
| optional | 4 |
| potential | 6 |

## CVE Findings (Dependency Vulnerabilities)

### CVE-2025-59250: JDBC Driver for SQL Server has improper input validation issue
- **Severity:** mandatory
- **Story Points:** 1
- **Files:** build.gradle:19

[CVE-2025-59250](https://github.com/advisories/GHSA-m494-w24q-6f7w): JDBC Driver for SQL Server has improper input validation issue

Severity: HIGH

Improper input validation in JDBC Driver for SQL Server allows an unauthorized attacker to perform spoofing over a network.

Affected dependencies:
  - com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre8 (declared at build.gradle:19)

Recommended fix:
  - Upgrade com.microsoft.sqlserver:mssql-jdbc to 12.8.2.jre8 or later

## CWE Findings (Code-Level Vulnerabilities)

### CWE-79: Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')
- **Category:** Injection Attacks
- **Severity:** optional
- **Story Points:** 8
- **Files:** src/main/webapp/WEB-INF/jsp/reports.jsp

In reports.jsp, database-sourced values are rendered using unescaped JSP EL expressions. At line 12, `${username}` outputs the authenticated user's username directly into HTML without encoding. At lines 51–55, `${report.reportId}`, `${report.reportType}`, `${report.filingDate}`, `${report.status}`, and `${report.filedBy}` are rendered without HTML escaping inside a `c:forEach` loop. JSP EL expressions (`${...}`) do not automatically HTML-encode output; if any database-stored value contains HTML or JavaScript, it will be rendered verbatim, enabling stored Cross-Site Scripting attacks.

---

### CWE-477: Use of Obsolete Function
- **Category:** Code Quality
- **Severity:** optional
- **Story Points:** 1
- **Files:** src/main/java/com/zavabank/compliancereporter/ComplianceConnectionFactory.java

In ComplianceConnectionFactory.java at line 10, the code uses `Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")` to explicitly register the JDBC driver. This pattern has been obsolete since JDBC 4.0 (Java 6): drivers that follow the JDBC 4.0 specification are automatically discovered and registered via the ServiceLoader mechanism, making the explicit `Class.forName()` call unnecessary and an indicator of outdated code.

---

### CWE-772: Missing Release of Resource after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/compliancereporter/SsoSessionService.java

In SsoSessionService.java at line 93, an `HttpURLConnection` is opened via `new URL(whoAmIUrl).openConnection()` but `conn.disconnect()` is never called after use. The connection is not wrapped in a try-finally or try-with-resources block, so it may remain open and unreleased after the method returns, causing a resource leak.

---

### CWE-775: Missing Release of File Descriptor or Handle after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/compliancereporter/ComplianceConfig.java

In ComplianceConfig.java at lines 12–15, the `InputStream` returned by `getResourceAsStream("compliance.properties")` is not managed within a try-with-resources block. If `PROPERTIES.load(inputStream)` throws a RuntimeException, the stream's `close()` on line 15 will not be reached, leaving the file handle unreleased.

---

### CWE-1057: Data Access Operations Outside of Expected Data Manager Component
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 5
- **Files:** src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java, src/main/java/com/zavabank/compliancereporter/ReportsServlet.java, src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java, src/main/java/com/zavabank/compliancereporter/SsoSessionService.java

Although `ComplianceConnectionFactory` serves as a connection factory, there is no centralized data manager or repository layer. SQL queries and `ResultSet` handling are embedded directly in servlet classes (`GenerateReportServlet`, `ReportsServlet`, `DownloadReportServlet`) and in the service class `SsoSessionService`, violating separation of concerns and the expectation that all data access is routed through a dedicated data manager component.

---

### CWE-259: Use of Hard-coded Password
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/compliance.properties

In compliance.properties at line 5, the SQL Server database password is hard-coded. While `ComplianceConfig.java` reads this value as a fallback when the `DB_PASSWORD` environment variable is not set, the password is committed in the source code, making it accessible to anyone with repository access.

---

### CWE-778: Insufficient Logging
- **Category:** Credentials & Secrets
- **Severity:** potential
- **Story Points:** 3
- **Files:** src/main/java/com/zavabank/compliancereporter/SsoSessionService.java, src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java, src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java, src/main/java/com/zavabank/compliancereporter/ReportsServlet.java

Security-critical events are silently suppressed with empty catch blocks throughout the codebase. In `SsoSessionService.java` at line 38, authentication failures (`SQLException`) are silently ignored with no log entry, making it impossible to detect failed login attempts or brute-force attacks. In `DownloadReportServlet.java` at line 35, database access failures during report retrieval are swallowed. In `GenerateReportServlet.java` at line 112, report save failures are ignored. In `ReportsServlet.java` at line 44, report-listing query failures are discarded. No logging framework is used anywhere in the application.

---

### CWE-798: Use of Hard-coded Credentials
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** src/main/resources/compliance.properties

In compliance.properties, the SQL Server database credentials are hard-coded: `db.user=sa` (line 4) and a database password (line 5). Using the `sa` (system administrator) account with a committed password in source control exposes the highest-privilege database account to anyone with read access to the repository.

---

### CWE-567: Unsynchronized Access to Shared Data in a Multithreaded Context
- **Category:** Concurrency & Synchronization
- **Severity:** potential
- **Story Points:** 5
- **Files:** src/main/java/com/zavabank/compliancereporter/ReportsServlet.java

In `ReportsServlet.java` at line 17, a `SimpleDateFormat` instance is declared as a `private static final` field. `SimpleDateFormat` is not thread-safe — its internal `Calendar` and `NumberFormat` state is mutated during formatting. In a servlet container, the same servlet instance handles multiple concurrent requests on different threads; each concurrent call to `DATE_FORMAT.format(filingDate)` at line 39 may corrupt the shared internal state, producing garbled date strings or throwing exceptions.

---

### CWE-820: Missing Synchronization
- **Category:** Concurrency & Synchronization
- **Severity:** potential
- **Story Points:** 8
- **Files:** src/main/java/com/zavabank/compliancereporter/ReportsServlet.java

The static `SimpleDateFormat DATE_FORMAT` field in `ReportsServlet.java` (line 17) is a shared mutable resource accessed concurrently by multiple servlet request-handling threads. No synchronization (synchronized block, `ThreadLocal`, or thread-safe alternative such as `DateTimeFormatter`) is used around its access at line 39, violating the requirement to synchronize access to a shared mutable resource in a multithreaded servlet environment.
