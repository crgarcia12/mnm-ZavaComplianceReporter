# Security Assessment Report

**Generated:** 2026-05-29T03:52:13.0000000Z

## Summary

| Metric | Count |
|--------|-------|
| Total Findings | 10 |
| CVE Vulnerabilities | 0 |
| CWE Vulnerabilities | 10 |
| Total Rules Assessed | 59 |
| Rules Passed | 49 |

### By Severity

| Severity | Count |
|----------|-------|
| mandatory | 0 |
| optional | 4 |
| potential | 6 |

### By Category

| Category | Count |
|----------|-------|
| Injection Attacks | 1 |
| Code Quality | 4 |
| Credentials & Secrets | 3 |
| Concurrency & Synchronization | 2 |

## CVE Findings (Dependency Vulnerabilities)

No CVE vulnerabilities found in project dependencies. All 3 declared dependencies (javax.servlet-api 3.1.0, jstl 1.2, mssql-jdbc 12.8.1.jre8) have no known CVEs at or above the `high` severity threshold.

## CWE Findings (Code-Level Vulnerabilities)

### CWE-79: Improper Neutralization of Input During Web Page Generation ('Cross-site Scripting')
- **Category:** Injection Attacks
- **Severity:** optional
- **Story Points:** 8
- **Files:** `src/main/webapp/WEB-INF/jsp/reports.jsp`

In reports.jsp, several database-sourced values are rendered into HTML using bare JSP EL expressions (e.g., `${username}` at line 12, `${report.reportType}` at line 52, `${report.filedBy}` at line 55) without HTML escaping. JSP EL expressions (`${}`) do not escape HTML characters by default — only `<c:out value="${...}"/>` provides safe output encoding. If a malicious value is stored in the `Users.Username`, `ComplianceReports.ReportType`, or `ComplianceReports.FiledBy` columns, it would be rendered as raw HTML in every officer's browser, enabling stored Cross-Site Scripting.

---

### CWE-259: Use of Hard-coded Password
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** `src/main/resources/compliance.properties`

`compliance.properties` line 5 contains a hard-coded plaintext database password committed directly to source control. The `ComplianceConfig.getDbPassword()` method reads this value as the default when the `DB_PASSWORD` environment variable is not set, meaning the production database password ships embedded in the WAR artifact. The database user is `sa` (the SQL Server system administrator account), compounding the severity of the exposed credential.

---

### CWE-477: Use of Obsolete Function
- **Category:** Code Quality
- **Severity:** optional
- **Story Points:** 1
- **Files:** `src/main/java/com/zavabank/compliancereporter/ComplianceConnectionFactory.java`, `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java`

Two obsolete API usages found: (1) `ComplianceConnectionFactory.java` line 10 uses `Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")` — obsolete since JDBC 4.0 (Java 6). (2) `ReportsServlet.java` line 17 uses `SimpleDateFormat` as a static field — not thread-safe and superseded by `DateTimeFormatter` since Java 8.

---

### CWE-567: Unsynchronized Access to Shared Data in a Multithreaded Context
- **Category:** Concurrency & Synchronization
- **Severity:** potential
- **Story Points:** 5
- **Files:** `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java`

`ReportsServlet.DATE_FORMAT` (line 17) is a `static final SimpleDateFormat` instance accessed by concurrent request threads without synchronization. `SimpleDateFormat` is not thread-safe. Concurrent calls to `DATE_FORMAT.format(filingDate)` at line 39 can produce corrupted date strings or throw `ArrayIndexOutOfBoundsException`.

---

### CWE-772: Missing Release of Resource after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java`

In `SsoSessionService.resolveTokenFromAuthGateway()` (lines 93–114), an `HttpURLConnection` is opened but never explicitly disconnected via `conn.disconnect()`. In the early-return path (`if (status != 200) { return ""; }`) and in the normal completion path, TCP socket resources are not explicitly released, relying on JVM finalizer behavior.

---

### CWE-775: Missing Release of File Descriptor or Handle after Effective Lifetime
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 3
- **Files:** `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java`

In `SsoSessionService.resolveTokenFromAuthGateway()` (line 93), the `HttpURLConnection` handle is opened but `conn.disconnect()` is never called in any code path, leaving the underlying socket file descriptor unreleased.

---

### CWE-778: Insufficient Logging
- **Category:** Credentials & Secrets
- **Severity:** potential
- **Story Points:** 3
- **Files:** `src/main/java/com/zavabank/compliancereporter/AuthFilter.java`, `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java`, `src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java`

No logging framework is present in the application. All exception handlers use empty `catch (Exception ignored)` bodies, silently discarding all errors. Security-critical events (failed authentication, unauthorized access attempts, report generation failures) go completely unrecorded, making security incident detection, forensic investigation, and compliance auditing impossible.

---

### CWE-798: Use of Hard-coded Credentials
- **Category:** Credentials & Secrets
- **Severity:** optional
- **Story Points:** 5
- **Files:** `src/main/resources/compliance.properties`

`compliance.properties` contains hard-coded SQL Server SA credentials (`db.user=sa` at line 4, `db.password` at line 5) committed to source control. These credentials ship embedded in every WAR artifact and serve as the default for all database connections when environment variables are not set.

---

### CWE-820: Missing Synchronization
- **Category:** Concurrency & Synchronization
- **Severity:** potential
- **Story Points:** 8
- **Files:** `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java`

The static `DATE_FORMAT` field (`ReportsServlet` line 17) is accessed in `loadReports()` by concurrent request threads with no synchronization mechanism — no `synchronized` block, no `volatile`, no `ThreadLocal`. Access to this non-thread-safe shared resource is completely unsynchronized.

---

### CWE-1057: Data Access Operations Outside of Expected Data Manager Component
- **Category:** Code Quality
- **Severity:** potential
- **Story Points:** 5
- **Files:** `src/main/java/com/zavabank/compliancereporter/GenerateReportServlet.java`, `src/main/java/com/zavabank/compliancereporter/ReportsServlet.java`, `src/main/java/com/zavabank/compliancereporter/DownloadReportServlet.java`, `src/main/java/com/zavabank/compliancereporter/SsoSessionService.java`

All four data-accessing classes bypass a dedicated DAO/Repository layer and directly construct `PreparedStatement` and execute SQL inline within their business/servlet logic. SQL queries are scattered across `GenerateReportServlet` (lines 54–57, 82–87, 105–108), `ReportsServlet` (lines 31–32), `DownloadReportServlet` (lines 26–27), and `SsoSessionService` (lines 27–31) with no centralized data access component.
