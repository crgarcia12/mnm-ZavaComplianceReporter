<%@ page contentType="text/html; charset=UTF-8" %>
<html>
<head>
    <title>ZavaComplianceReporter Login</title>
</head>
<body bgcolor="#f4f4f4">
<table width="760" align="center" cellpadding="8" cellspacing="0" border="1" bgcolor="#ffffff">
    <tr bgcolor="#003333">
        <td><font color="#ffffff"><b>ZavaComplianceReporter - Regulatory Reporting Console</b></font></td>
    </tr>
    <tr bgcolor="#c8d8d8">
        <td style="padding:3px 8px;font-size:11px;font-family:Verdana,Arial;"><a href="/" style="color:#003333;text-decoration:none;font-weight:bold;">&#9664; ZavaBank Portal</a></td>
    </tr>
    <tr>
        <td>
            <p>Authenticate through shared SessionTokens SSO bridge.</p>
            <%
                Object loginError = request.getAttribute("loginError");
                if (loginError != null) {
            %>
            <p><font color="#cc0000"><b><%= loginError %></b></font></p>
            <%
                }
            %>
            <form method="post" action="<%= request.getContextPath() %>/login">
                <table cellpadding="6" cellspacing="0" border="0">
                    <tr>
                        <td><b>Session Token</b></td>
                        <td><input type="text" name="sessionToken" size="58" maxlength="128"/></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td><input type="submit" value="Sign In"/></td>
                    </tr>
                </table>
            </form>
            <p><small>Token can come from .ZAVAAUTH cookie, query string, or X-Session-Token header.</small></p>
        </td>
    </tr>
</table>
</body>
</html>
