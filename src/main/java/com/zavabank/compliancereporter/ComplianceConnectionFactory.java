package com.zavabank.compliancereporter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ComplianceConnectionFactory {
    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException("SQL Server JDBC driver not found", exception);
        }
    }

    private ComplianceConnectionFactory() {
    }

    public static Connection openConnection() throws SQLException {
        return DriverManager.getConnection(
            ComplianceConfig.getDbUrl(),
            ComplianceConfig.getDbUser(),
            ComplianceConfig.getDbPassword()
        );
    }
}
