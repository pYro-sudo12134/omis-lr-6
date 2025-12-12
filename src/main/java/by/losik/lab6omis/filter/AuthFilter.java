package by.losik.lab6omis.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        System.out.println("=== AUTH FILTER ===");
        System.out.println("Filter checking path: " + path);
        System.out.println("Context path: " + httpRequest.getContextPath());

        if (path.equals("/") || path.isEmpty() ||
                path.equals("/login") || path.equals("/logout") ||
                path.equals("/auth") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/images/") || path.startsWith("/api/")) {

            System.out.println("Allowing access to: " + path);
            chain.doFilter(request, response);
            return;
        }

        if (session != null) {
            Boolean isAuthenticated = (Boolean) session.getAttribute("authenticated");
            System.out.println("Session authenticated: " + isAuthenticated);

            if (isAuthenticated != null && isAuthenticated) {
                System.out.println("User authenticated, allowing: " + path);
                chain.doFilter(request, response);
                return;
            }
        }

        System.out.println("User NOT authenticated, redirecting to login");
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }

    @Override
    public void destroy() {
    }
}