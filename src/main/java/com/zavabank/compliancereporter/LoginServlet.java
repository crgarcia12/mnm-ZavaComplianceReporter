package com.zavabank.compliancereporter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private final SsoSessionService ssoSessionService = new SsoSessionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(true);
        if (session.getAttribute(SsoSessionService.SESSION_USER) instanceof SessionUser) {
            response.sendRedirect(request.getContextPath() + "/reports");
            return;
        }
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String explicitToken = request.getParameter("sessionToken");
        SessionUser user = ssoSessionService.resolveSessionUser(request, explicitToken);
        if (user != null) {
            request.getSession(true).setAttribute(SsoSessionService.SESSION_USER, user);
            response.sendRedirect(request.getContextPath() + "/reports");
            return;
        }

        request.setAttribute("loginError", "Session token was not found or has expired.");
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
    }
}
