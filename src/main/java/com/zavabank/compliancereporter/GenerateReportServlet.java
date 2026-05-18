package com.zavabank.compliancereporter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenerateReportServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionUser user = (SessionUser) request.getSession(true).getAttribute(SsoSessionService.SESSION_USER);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String reportType = normalize(request.getParameter("reportType"));
        if (!"CTR".equalsIgnoreCase(reportType) && !"SAR".equalsIgnoreCase(reportType)) {
            response.sendRedirect(request.getContextPath() + "/reports");
            return;
        }

        String reportData;
        if ("CTR".equalsIgnoreCase(reportType)) {
            BigDecimal threshold = parseThreshold(request.getParameter("thresholdAmount"));
            reportData = generateCtrData(threshold);
        } else {
            reportData = generateSarData();
        }

        saveReport(reportType.toUpperCase(), reportData, user.getUsername());
        response.sendRedirect(request.getContextPath() + "/reports");
    }

    private BigDecimal parseThreshold(String value) {
        try {
            return new BigDecimal(normalize(value));
        } catch (Exception ignored) {
            return new BigDecimal("10000.00");
        }
    }

    private String generateCtrData(BigDecimal threshold) {
        StringBuilder report = new StringBuilder();
        report.append("CURRENCY TRANSACTION REPORT").append('\n');
        report.append("Threshold: ").append(threshold.toPlainString()).append('\n');
        report.append("--------------------------------------------------").append('\n');

        try (Connection connection = ComplianceConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT TOP 200 t.TransactionID, a.AccountNumber, t.Amount, t.TransactionDate, t.Description " +
                     "FROM Transactions t INNER JOIN Accounts a ON t.AccountID = a.AccountID " +
                     "WHERE ABS(t.Amount) >= ? ORDER BY t.TransactionDate DESC")) {
            statement.setBigDecimal(1, threshold);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    report.append("TXN ").append(resultSet.getLong(1))
                        .append(" | ACCT ").append(resultSet.getString(2))
                        .append(" | AMT ").append(resultSet.getBigDecimal(3))
                        .append(" | DATE ").append(resultSet.getTimestamp(4))
                        .append(" | DESC ").append(resultSet.getString(5))
                        .append('\n');
                }
            }
        } catch (Exception exception) {
            report.append("Unable to retrieve large transactions.");
        }
        return report.toString();
    }

    private String generateSarData() {
        StringBuilder report = new StringBuilder();
        report.append("SUSPICIOUS ACTIVITY REPORT").append('\n');
        report.append("Severity: High/Critical and unresolved alerts").append('\n');
        report.append("--------------------------------------------------").append('\n');

        try (Connection connection = ComplianceConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT TOP 200 AlertID, TransactionID, Severity, Status, Description, CreatedDate " +
                     "FROM FraudAlerts " +
                     "WHERE Severity IN ('High', 'Critical') OR Status IN ('New', 'InReview') " +
                     "ORDER BY CreatedDate DESC");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                report.append("ALERT ").append(resultSet.getInt(1))
                    .append(" | TXN ").append(resultSet.getLong(2))
                    .append(" | SEV ").append(resultSet.getString(3))
                    .append(" | STATUS ").append(resultSet.getString(4))
                    .append(" | DATE ").append(resultSet.getTimestamp(6))
                    .append(" | DESC ").append(resultSet.getString(5))
                    .append('\n');
            }
        } catch (Exception exception) {
            report.append("Unable to retrieve suspicious activity alerts.");
        }
        return report.toString();
    }

    private void saveReport(String type, String reportData, String username) {
        try (Connection connection = ComplianceConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO ComplianceReports (ReportType, FilingDate, Status, ReportData, FiledBy, RegulatoryBody, CreatedDate) " +
                     "VALUES (?, GETDATE(), 'Generated', ?, ?, 'FinCEN', GETDATE())")) {
            statement.setString(1, type);
            statement.setString(2, reportData);
            statement.setString(3, username);
            statement.executeUpdate();
        } catch (Exception ignored) {
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
