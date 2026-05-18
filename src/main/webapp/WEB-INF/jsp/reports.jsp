<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>ZavaComplianceReporter - Reports</title>
</head>
<body bgcolor="#f4f4f4">
<table width="1060" align="center" cellpadding="8" cellspacing="0" border="1" bgcolor="#ffffff">
    <tr bgcolor="#003333">
        <td colspan="7">
            <font color="#ffffff"><b>ZavaComplianceReporter - CTR / SAR Management</b></font>
            <span style="float:right;color:#ffffff;">Officer: ${username}</span>
        </td>
    </tr>
    <tr bgcolor="#c8d8d8">
        <td colspan="7" style="padding:3px 8px;font-size:11px;font-family:Verdana,Arial;"><a href="/" style="color:#003333;text-decoration:none;font-weight:bold;">&#9664; ZavaBank Portal</a></td>
    </tr>
    <tr>
        <td colspan="7">
            <table width="100%" border="0" cellpadding="6" cellspacing="0">
                <tr>
                    <td width="50%" valign="top">
                        <b>Generate Currency Transaction Report (CTR)</b>
                        <form method="post" action="${pageContext.request.contextPath}/reports/generate">
                            <input type="hidden" name="reportType" value="CTR"/>
                            Threshold Amount: <input type="text" name="thresholdAmount" value="10000.00" size="12"/>
                            <input type="submit" value="Generate CTR"/>
                        </form>
                    </td>
                    <td width="50%" valign="top">
                        <b>Generate Suspicious Activity Report (SAR)</b>
                        <form method="post" action="${pageContext.request.contextPath}/reports/generate">
                            <input type="hidden" name="reportType" value="SAR"/>
                            <input type="submit" value="Generate SAR"/>
                        </form>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr bgcolor="#d7d7d7">
        <th align="left">Report ID</th>
        <th align="left">Type</th>
        <th align="left">Filing Date</th>
        <th align="left">Status</th>
        <th align="left">Filed By</th>
        <th align="left">Download</th>
    </tr>
    <c:forEach var="report" items="${reports}">
        <tr>
            <td>${report.reportId}</td>
            <td>${report.reportType}</td>
            <td>${report.filingDate}</td>
            <td>${report.status}</td>
            <td>${report.filedBy}</td>
            <td><a href="${pageContext.request.contextPath}/reports/download?reportId=${report.reportId}">Download</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty reports}">
        <tr>
            <td colspan="6"><i>No compliance reports are available yet.</i></td>
        </tr>
    </c:if>
</table>
</body>
</html>
