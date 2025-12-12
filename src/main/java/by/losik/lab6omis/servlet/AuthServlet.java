package by.losik.lab6omis.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet для аутентификации пользователей.
 * Обрабатывает запросы на вход в систему (POST) и выход из системы (GET).
 * Сравнивает переданные учетные данные с учетными данными администратора,
 * которые задаются через переменные окружения или значения по умолчанию.
 *
 * @author Losik Yarolav
 * @version 1.0
 */
public class AuthServlet extends HttpServlet {

    private static String ADMIN_USERNAME;
    private static String ADMIN_PASSWORD;

    /**
     * Инициализирует сервлет, загружая учетные данные администратора.
     * Значения берутся из переменных окружения ADMIN_USERNAME и ADMIN_PASSWORD.
     * Если переменные окружения не заданы, используются значения по умолчанию:
     * "admin" для имени пользователя и "admin" для пароля.
     */
    @Override
    public void init() {
        ADMIN_USERNAME = System.getenv("ADMIN_USERNAME");
        ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");
        if (ADMIN_USERNAME == null) ADMIN_USERNAME = "admin";
        if (ADMIN_PASSWORD == null) ADMIN_PASSWORD = "admin";
    }

    /**
     * Обрабатывает POST-запрос для аутентификации пользователя.
     * Сравнивает переданные имя пользователя и пароль с сохраненными учетными данными администратора.
     * В случае успешной аутентификации создает сессию и перенаправляет на страницу dashboard.
     * В случае неудачи перенаправляет обратно на страницу login с параметром error.
     *
     * @param req  HttpServletRequest объект, содержащий данные запроса (имя пользователя и пароль)
     * @param resp HttpServletResponse объект для отправки ответа клиенту
     * @throws IOException если происходит ошибка ввода-вывода при перенаправлении
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            HttpSession session = req.getSession();
            session.setAttribute("authenticated", true);
            session.setAttribute("username", username);

            String redirectPath = req.getContextPath() + "/dashboard";
            resp.sendRedirect(redirectPath);
        } else {
            String redirectPath = req.getContextPath() + "/login?error=true";
            resp.sendRedirect(redirectPath);
        }
    }

    /**
     * Обрабатывает GET-запрос для выхода из системы (logout).
     * Инвалидирует текущую сессию пользователя, если она существует,
     * и перенаправляет на страницу login.
     *
     * @param req  HttpServletRequest объект, содержащий данные запроса
     * @param resp HttpServletResponse объект для отправки ответа клиенту
     * @throws IOException если происходит ошибка ввода-вывода при перенаправлении
     */
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