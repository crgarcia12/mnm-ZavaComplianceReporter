package com.zavabank.compliancereporter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadReportServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reportIdParam = request.getParameter("reportId");
        int reportId;
        try {
            reportId = Integer.parseInt(reportIdParam);
        } catch (Exception ignored) {
            response.sendRedirect(request.getContextPath() + "/reports");
            return;
        }

        String reportType = "REPORT";
        String reportData = null;
        try (Connection connection = ComplianceConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT ReportType, ReportData FROM ComplianceReports WHERE ReportID = ?")) {
            statement.setInt(1, reportId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    reportType = resultSet.getString(1);
                    reportData = resultSet.getString(2);
                }
            }
        } catch (Exception ignored) {
        }

        if (reportData == null) {
            response.sendRedirect(request.getContextPath() + "/reports");
            return;
        }

        String filename = reportType + "-" + reportId + ".txt";
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.getWriter().write(reportData);
    }
}
