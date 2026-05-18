package com.zavabank.compliancereporter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ReportsServlet extends HttpServlet {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(true);
        SessionUser user = (SessionUser) session.getAttribute(SsoSessionService.SESSION_USER);
        request.setAttribute("username", user == null ? "Unknown" : user.getUsername());
        request.setAttribute("reports", loadReports());
        request.getRequestDispatcher("/WEB-INF/jsp/reports.jsp").forward(request, response);
    }

    private List<ReportRecord> loadReports() {
        List<ReportRecord> reports = new ArrayList<ReportRecord>();
        try (Connection connection = ComplianceConnectionFactory.openConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "SELECT TOP 100 ReportID, ReportType, FilingDate, Status, FiledBy FROM ComplianceReports ORDER BY FilingDate DESC");
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ReportRecord record = new ReportRecord();
                record.setReportId(resultSet.getInt(1));
                record.setReportType(resultSet.getString(2));
                java.util.Date filingDate = resultSet.getTimestamp(3);
                record.setFilingDate(filingDate == null ? "" : DATE_FORMAT.format(filingDate));
                record.setStatus(resultSet.getString(4));
                record.setFiledBy(resultSet.getString(5));
                reports.add(record);
            }
        } catch (java.sql.SQLException ignored) {
        }
        return reports;
    }
}
