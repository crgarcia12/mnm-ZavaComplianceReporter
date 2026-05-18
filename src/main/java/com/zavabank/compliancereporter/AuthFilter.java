package com.zavabank.compliancereporter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {
    private final SsoSessionService ssoSessionService = new SsoSessionService();

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(true);
        Object existing = session.getAttribute(SsoSessionService.SESSION_USER);
        if (existing instanceof SessionUser) {
            chain.doFilter(request, response);
            return;
        }

        SessionUser resolved = ssoSessionService.resolveSessionUser(httpRequest, "");
        if (resolved != null) {
            session.setAttribute(SsoSessionService.SESSION_USER, resolved);
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    @Override
    public void destroy() {
    }
}
