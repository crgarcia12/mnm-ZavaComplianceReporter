# Modernization Plan: Zava Compliance Reporter modernization to Azure

**Project**: Zava Compliance Reporter

---

## Technical Framework

- **Language**: Java 8
- **Framework**: Java EE Servlet/JSP (javax.servlet 3.1)
- **Build Tool**: Gradle (war packaging)
- **Database**: Microsoft SQL Server
- **Key Dependencies**: javax.servlet-api, JSTL, mssql-jdbc, Tomcat runtime

---

## Overview

> This migration modernizes the Zava Compliance Reporter application and deploys it to Azure. The application currently runs on a legacy Java 8 Servlet/JSP stack with SQL Server connectivity and container-based deployment. The new architecture will:
>
> - Upgrade the application framework/runtime to a current supported Java platform
> - Move core runtime dependencies to Azure cloud-native managed services
> - Deploy the modernized workload to Azure using a cloud-native deployment target
>
> The migration follows a phased approach: baseline and upgrade first, service transformation second, security remediation third, and production deployment last.

---

## Migration Impact Summary

| Application | Original Service | New Azure Service | Authentication | Comments |
|-------------|------------------|-------------------|----------------|----------|
| Zava Compliance Reporter | Legacy Java 8 runtime | Modern Java runtime on Azure | Managed Identity | Upgrade to latest framework/runtime baseline |
| Zava Compliance Reporter | SQL Server connectivity and local app config | Azure SQL + Key Vault + App Configuration | Managed Identity | Modernize to cloud-native managed services |
| Zava Compliance Reporter | Current container hosting model | Azure Container Apps | Managed Identity | Deploy modernized app to Azure |

