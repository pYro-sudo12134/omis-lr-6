package by.losik.lab6omis.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AuthServlet extends HttpServlet {

    private static String ADMIN_USERNAME;
    private static String ADMIN_PASSWORD;

    @Override
    public void init() {
        ADMIN_USERNAME = System.getenv("ADMIN_USERNAME");
        ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");
        if (ADMIN_USERNAME == null) ADMIN_USERNAME = "admin";
        if (ADMIN_PASSWORD == null) ADMIN_PASSWORD = "admin";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            System.out.println("DEBUG: Login successful!");
            HttpSession session = req.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("username", username);

            String redirectPath = req.getContextPath() + "/dashboard";
            System.out.println("DEBUG: Redirecting to dashboard: " + redirectPath);
            resp.sendRedirect(redirectPath);
        } else {
            System.out.println("DEBUG: Login failed!");
            String redirectPath = req.getContextPath() + "/login?error=true";
            System.out.println("DEBUG: Redirecting to login with error: " + redirectPath);
            resp.sendRedirect(redirectPath);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}