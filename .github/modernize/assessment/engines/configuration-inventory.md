# Configuration & Externalized Settings Inventory

ZavaComplianceReporter has a minimal, flat configuration landscape: a single bundled properties file (`compliance.properties`) with environment-variable overrides is the only configuration source, and there are no runtime profiles, feature flags, or external secret stores.

## Configuration Sources

| Source | Type | Path/Location | Notes |
|---|---|---|---|
| `compliance.properties` | Java properties file (bundled) | `src/main/resources/compliance.properties` | Loaded at class initialization via `ComplianceConfig`; provides DB connection parameters and SSO settings |
| `web.xml` | Java EE deployment descriptor | `src/main/webapp/WEB-INF/web.xml` | Declares Servlet and Filter registrations and URL mappings; not externalized — requires a rebuild to change |
| `Dockerfile` | Container build & runtime config | `Dockerfile` | Multi-stage build (Gradle 7.6/JDK 8 → Tomcat 9/JDK 8); `EXPOSE 8080`; no runtime env-var injection |
| Environment variables | OS / container env | Runtime injection | Seven env vars override the bundled properties file values at runtime |

No Spring Cloud Config server, Azure App Configuration, AWS AppConfig, Consul KV, HashiCorp Vault, or Kubernetes ConfigMaps/Secrets are referenced.

## Build Profiles

| Profile | Activation | Purpose | Key Dependencies/Plugins |
|---|---|---|---|
| (default / only) | Automatic — `gradle war` | Compile Java sources, package as `ROOT.war` for Tomcat | `java` plugin, `war` plugin; `javax.servlet-api` (compileOnly), `jstl`, `mssql-jdbc` |

Gradle does not declare any named build types, flavors, or environment-specific build profiles. All configuration variation is handled at runtime via environment variables.

## Runtime Profiles

No runtime profiles are configured. There are no `application-{profile}.properties` files, `@Profile` annotations, `spring.profiles.active` settings, or equivalent profile-switching mechanisms. A single flat configuration is used in all environments; environment-specific values must be supplied via environment variables.

| Profile | Activation Method | Config Files | Key Overrides |
|---|---|---|---|
| (none — single profile) | N/A | `compliance.properties` | Override any property via the corresponding environment variable (see Properties Inventory) |

## Properties Inventory

### ZavaComplianceReporter — `compliance.properties`

| Property Key | Default Value | Environment Variable Override | Notes |
|---|---|---|---|
| `db.host` | `sqlserver` | `DB_HOST` | Hostname of the SQL Server instance |
| `db.port` | `1433` | `DB_PORT` | TCP port for SQL Server |
| `db.name` | `ZavaBankDB` | `DB_NAME` | Database name |
| `db.user` | `sa` | `DB_USER` | Database login username |
| `db.password` | `Zava123!` | `DB_PASSWORD` | Database login password — **hardcoded in source; see Secrets section** |
| `sso.cookie.name` | `.ZAVAAUTH` | `SSO_COOKIE_NAME` | Name of the .NET FormsAuth cookie forwarded to the Auth Gateway |
| `auth.gateway.whoami.url` | `http://zava-auth-gateway:8080/WhoAmI.ashx` | `AUTH_GATEWAY_WHOAMI_URL` | Full URL of the external SSO auth gateway WhoAmI endpoint |

Resolution priority: environment variable (non-empty) → `compliance.properties` key → empty string (no default fallback for missing properties). The JDBC URL is assembled as `jdbc:sqlserver://<DB_HOST>:<DB_PORT>;databaseName=<DB_NAME>;encrypt=false;trustServerCertificate=true`.

## Startup Parameters & Resource Requirements

| Service | JVM/Runtime Options | Memory | Instance Count |
|---|---|---|---|
| ZavaComplianceReporter (Tomcat) | None specified — Tomcat defaults (`catalina.sh run`) | Not specified — no Docker `mem_limit`, no Kubernetes resource limits | 1 (single container) |

No `-Xms`/`-Xmx` JVM heap settings, `-D` system properties, or resource constraints are declared anywhere in the build or container configuration. Tomcat uses its built-in default heap sizing based on available JVM/OS memory.

## Startup Dependency Chain

```
SQL Server (ZavaBankDB)
  └── ZavaComplianceReporter (Tomcat WAR)
        └── Auth Gateway (zava-auth-gateway:8080) [required only for .ZAVAAUTH SSO cookie resolution]
```

No wait-for-TCP (`dockerize`), Docker Compose `depends_on` with health checks, Kubernetes readiness probes, or any startup-sequencing mechanism is defined. The application does **not** verify database connectivity or Auth Gateway availability on startup. If SQL Server is unavailable, the application starts successfully but all DB-backed operations silently fail (exceptions are caught and ignored). If the Auth Gateway is unavailable, SSO cookie-based login silently fails (3-second timeout) and users are redirected to the login page.

There is no health-check liveness mechanism beyond the basic `GET /health` HTML endpoint, which always returns HTTP 200 regardless of database connectivity.

## Secrets & Sensitive Configuration

| Secret Reference | Type | Storage | Notes |
|---|---|---|---|
| `db.password` / `DB_PASSWORD` | Database password | Hardcoded in `compliance.properties` as `[MASKED]` | Plaintext in source file; no encryption (Jasypt, sealed secrets, etc.) |
| `db.user` / `DB_USER` | Database username | Hardcoded in `compliance.properties` as `sa` | Uses the SQL Server system administrator account |
| `auth.gateway.whoami.url` / `AUTH_GATEWAY_WHOAMI_URL` | External service URL (not a secret per se) | Hardcoded in `compliance.properties` | Uses plain HTTP (not HTTPS) |

### Secrets Provisioning Workflow

**Current state**: Secrets are hardcoded in the bundled `compliance.properties` file committed to source control. There is no secrets management workflow. The only override mechanism is environment variables injected at container/process startup — but no provisioning pipeline or secret store integration (HashiCorp Vault, Azure Key Vault, AWS Secrets Manager, Kubernetes Secrets) is configured.

**Required provisioning steps** (as-is):
1. Operator sets environment variables (`DB_PASSWORD`, `DB_USER`, etc.) in the container runtime (Docker run command, Kubernetes Pod spec, or cloud hosting platform).
2. `ComplianceConfig` reads the env var at class-load time and uses it in place of the bundled plaintext value.
3. If no env var is set, the hardcoded plaintext value from `compliance.properties` is used — this is the default behavior in development and any environment where env vars are not explicitly injected.

No managed identities, service principals, RBAC roles, or audit trail for secret access are in place.

## Feature Flags

No feature flag framework is present. No `@ConditionalOnProperty`, `@ConditionalOnExpression`, LaunchDarkly, Unleash, or any other feature toggle mechanism is used. All application behavior is fixed at compile time.

| Flag Name | Default | Controlled By |
|---|---|---|
| (none detected) | — | — |

## Framework & Runtime Versions

| Component | Version | Source |
|---|---|---|
| Java (source/target) | 1.8 (Java 8) | `build.gradle` (`sourceCompatibility`/`targetCompatibility`) |
| Gradle | 7.6 | `Dockerfile` (`gradle:7.6-jdk8`) |
| Servlet API | 3.1.0 | `build.gradle` dependency |
| JSTL | 1.2 | `build.gradle` dependency |
| MS SQL Server JDBC Driver | 12.8.1.jre8 | `build.gradle` dependency |
| Apache Tomcat | 9.x (JDK 8 variant) | `Dockerfile` (`tomcat:9-jdk8`) |
| Build JDK (image) | JDK 8 (via `gradle:7.6-jdk8`) | `Dockerfile` (build stage) |
| Runtime JDK (image) | JDK 8 (via `tomcat:9-jdk8`) | `Dockerfile` (runtime stage) |
