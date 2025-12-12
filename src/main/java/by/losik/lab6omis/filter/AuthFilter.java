package by.losik.lab6omis.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Фильтр аутентификации для контроля доступа к защищенным страницам приложения.
 * Проверяет наличие активной сессии и аутентификации пользователя.
 * Разрешает доступ к публичным ресурсам (CSS, JS, изображения, API, страница входа)
 * без проверки аутентификации.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class AuthFilter implements Filter {

    /**
     * Основной метод фильтрации запросов.
     * Проверяет путь запроса и наличие аутентификации в сессии.
     * Перенаправляет неаутентифицированных пользователей на страницу входа.
     *
     * @param request  ServletRequest объект входящего запроса
     * @param response ServletResponse объект для ответа
     * @param chain    FilterChain для передачи запроса следующему фильтру или сервлету
     * @throws IOException      если происходит ошибка ввода-вывода при перенаправлении
     * @throws ServletException если происходит ошибка сервлета
     */
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

        // Разрешаем доступ к публичным ресурсам без аутентификации
        if (path.equals("/") || path.isEmpty() ||
                path.equals("/login") || path.equals("/logout") ||
                path.equals("/auth") ||
                path.startsWith("/css/") || path.startsWith("/js/") ||
                path.startsWith("/images/") || path.startsWith("/api/")) {

            System.out.println("Allowing access to: " + path);
            chain.doFilter(request, response);
            return;
        }

        // Проверяем аутентификацию для защищенных путей
        if (session != null) {
            Boolean isAuthenticated = (Boolean) session.getAttribute("authenticated");
            System.out.println("Session authenticated: " + isAuthenticated);

            if (isAuthenticated != null && isAuthenticated) {
                System.out.println("User authenticated, allowing: " + path);
                chain.doFilter(request, response);
                return;
            }
        }

        // Если пользователь не аутентифицирован, перенаправляем на страницу входа
        System.out.println("User NOT authenticated, redirecting to login");
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
    }
}