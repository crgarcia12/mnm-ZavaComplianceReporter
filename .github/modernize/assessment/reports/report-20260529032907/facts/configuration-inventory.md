# Configuration & Externalized Settings Inventory

ZavaComplianceReporter has a minimal configuration landscape: a single classpath properties file (`compliance.properties`) with 7 properties, each overridable via environment variables — there are no runtime profiles, no external config server, and no secret store integration.

## Configuration Sources

| Source | Type | Path / Location | Notes |
|--------|------|-----------------|-------|
| `compliance.properties` | Classpath properties file | `src/main/resources/compliance.properties` | Loaded statically at class-initialization via `ComplianceConfig` static block; all 7 properties overridable via environment variables |
| `web.xml` | Servlet deployment descriptor | `src/main/webapp/WEB-INF/web.xml` | Defines filter mappings, servlet registrations, and URL patterns; no environment-specific overrides |
| `build.gradle` | Gradle build file | `build.gradle` | Declares source/target Java version, WAR packaging, and all dependencies |
| `Dockerfile` | Docker build / runtime | `Dockerfile` | Two-stage build; defines the runtime image and exposed port |

No Spring Cloud Config server, Azure App Configuration, Consul KV, HashiCorp Vault, or AWS Secrets Manager integration is present.

## Build Profiles

| Profile | Activation | Purpose | Key Dependencies / Plugins |
|---------|-----------|---------|---------------------------|
| Default (single profile) | Always active | Compiles Java 8 source, packages as a WAR named `ROOT.war` | `javax.servlet-api:3.1.0` (provided), `javax.servlet:jstl:1.2`, `mssql-jdbc:12.8.1.jre8` |

No Maven profiles, Gradle build variants, or conditional plugin activations are defined. The `war { archiveBaseName = 'ROOT' }` configuration ensures the WAR is deployed as Tomcat's root web application.

## Runtime Profiles

No Spring or Java EE runtime profiles are configured. There is no `application-dev.properties`, `application-prod.properties`, or equivalent mechanism. Configuration differences between environments are handled exclusively via environment variable overrides of the 7 properties in `compliance.properties`.

| Profile | Activation | Config Files | Key Overrides |
|---------|-----------|-------------|---------------|
| N/A — single profile | N/A | `compliance.properties` (classpath, always loaded) | DB host/port/name/user/password and SSO settings via environment variables |

## Properties Inventory

### ZavaComplianceReporter — `compliance.properties`

| Property Key | Default Value | Environment Variable Override | Type | Notes |
|-------------|---------------|------------------------------|------|-------|
| `db.host` | `sqlserver` | `DB_HOST` | String | SQL Server hostname; default assumes Docker service name `sqlserver` |
| `db.port` | `1433` | `DB_PORT` | Integer | SQL Server default port |
| `db.name` | `ZavaBankDB` | `DB_NAME` | String | Target database name |
| `db.user` | `sa` | `DB_USER` | String | SQL Server login username |
| `db.password` | `Zava123!` | `DB_PASSWORD` | String | SQL Server login password — **sensitive; hardcoded in classpath file** |
| `sso.cookie.name` | `.ZAVAAUTH` | `SSO_COOKIE_NAME` | String | Name of the .NET FormsAuthentication cookie to extract for SSO |
| `auth.gateway.whoami.url` | `http://zava-auth-gateway:8080/WhoAmI.ashx` | `AUTH_GATEWAY_WHOAMI_URL` | String | Auth gateway endpoint for cookie-to-token resolution; default assumes Docker service name |

**JDBC URL construction** (derived from above, not a separate property):
`jdbc:sqlserver://<db.host>:<db.port>;databaseName=<db.name>;encrypt=false;trustServerCertificate=true`

No `web.xml` context parameters, JNDI data sources, or JVM system properties are read by the application.

## Startup Parameters & Resource Requirements

| Service | JVM / Runtime Options | Memory Allocation | CPU Allocation | Instance Count |
|---------|----------------------|-------------------|---------------|----------------|
| ZavaComplianceReporter (Tomcat 9) | JVM defaults (no `-Xms`/`-Xmx` specified in Dockerfile or wrapper scripts) | Not specified — Tomcat defaults apply | Not specified | 1 (no scaling configuration) |

No `JAVA_OPTS`, `CATALINA_OPTS`, or heap-tuning environment variables are set in the Dockerfile. Resource limits are not defined in any detected Docker Compose or Kubernetes manifest.

## Startup Dependency Chain

The application has no explicit startup health-check or dependency-wait mechanism. Services are expected to be available before the container starts processing requests:

1. **SQL Server (`ZavaBankDB`)** — must be reachable before any request that queries the database. No `dockerize` wait, no readiness probe beyond the custom `/health` endpoint (which does NOT verify DB connectivity).
2. **ZavaBank Auth Gateway (`WhoAmI.ashx`)** — must be reachable for SSO via `.ZAVAAUTH` cookie. A 3-second connect/read timeout is hardcoded; failure falls through to a direct session-token DB lookup.
3. **Tomcat 9** — starts with no precondition checks; first failure is reported at request time, not at startup.

There is no Docker Compose `depends_on`, Kubernetes readiness probe verifying downstream services, or Spring Cloud Config retry policy.

## Secrets & Sensitive Configuration

| Secret Reference | Type | Default / Storage | Notes |
|-----------------|------|------------------|-------|
| `db.password` / `DB_PASSWORD` | Database password | `Zava123!` in classpath file [MASKED] | Hardcoded plaintext in `compliance.properties`; overridable via `DB_PASSWORD` environment variable but no secret manager integration exists |
| `db.user` / `DB_USER` | Database username | `sa` (SQL Server system admin) | Running as `sa` is a security risk; should use a least-privilege account |
| `auth.gateway.whoami.url` | Service endpoint containing implicit trust | `http://zava-auth-gateway:8080/WhoAmI.ashx` | Plaintext HTTP (no TLS); URL default is hardcoded |

No encryption libraries (Jasypt, DPAPI, sealed secrets) are configured. No HashiCorp Vault, Azure Key Vault, or AWS Secrets Manager integration is present.

### Secrets Provisioning Workflow

**Current state**: Secrets are provisioned by embedding plaintext values in `compliance.properties` (committed to the repository) or by injecting them as plain environment variables at container startup. There is no formal secrets provisioning workflow — no managed identity, no RBAC-scoped Key Vault access, and no CI/CD pipeline secret binding.

**Recommended direction for cloud deployment**:
- Remove `db.password`, `db.user`, and `auth.gateway.whoami.url` from the committed properties file.
- Inject sensitive values at runtime via environment variables sourced from a secret store (e.g., Azure Key Vault via a managed identity, or Kubernetes Secrets mounted as env vars).
- Grant the application a least-privilege database account (not `sa`).
- Enforce TLS on the auth gateway URL.

## Feature Flags

No feature flag frameworks (LaunchDarkly, Unleash, Spring Feature Flags, .NET FeatureManagement) are used. No `@ConditionalOnProperty` annotations or A/B testing configurations are present.

| Flag Name | Default | Controlled By |
|-----------|---------|--------------|
| N/A | N/A | N/A |

## Framework & Runtime Versions

| Component | Version | Source |
|-----------|---------|--------|
| Java source / target compatibility | 8 | `build.gradle`: `sourceCompatibility = JavaVersion.VERSION_1_8` |
| Gradle (build tool) | 7.6 | `Dockerfile` build stage: `gradle:7.6-jdk8` |
| Gradle (local wrapper) | 8.9 (cached) | `.gradle/8.9/` directory |
| Apache Tomcat (runtime) | 9 | `Dockerfile` runtime stage: `tomcat:9-jdk8` |
| JDK (build stage) | 8 | `Dockerfile`: `gradle:7.6-jdk8` |
| JDK (runtime stage) | 8 | `Dockerfile`: `tomcat:9-jdk8` |
| Servlet API | 3.1.0 (javax) | `build.gradle`: `compileOnly 'javax.servlet:javax.servlet-api:3.1.0'` |
| JSTL | 1.2 (javax) | `build.gradle`: `implementation 'javax.servlet:jstl:1.2'` |
| Microsoft JDBC Driver for SQL Server | 12.8.1 (jre8) | `build.gradle`: `implementation 'com.microsoft.sqlserver:mssql-jdbc:12.8.1.jre8'` |
