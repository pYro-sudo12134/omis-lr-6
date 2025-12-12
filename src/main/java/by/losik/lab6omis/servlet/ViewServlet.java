package by.losik.lab6omis.servlet;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.entities.general.types.Sound;
import by.losik.lab6omis.service.general.types.AnalysisCommand;
import by.losik.lab6omis.service.general.types.RequestService;
import by.losik.lab6omis.service.general.types.SensorService;
import by.losik.lab6omis.service.general.types.SolutionService;
import by.losik.lab6omis.service.general.types.SoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Основной сервлет для отображения и обработки всех страниц веб-приложения.
 * Использует шаблонизатор Thymeleaf для рендеринга HTML-страниц.
 * Обрабатывает запросы на CRUD-операции для сущностей Sensor, Sound и Solution,
 * а также управляет анализом данных и диалоговой системой.
 *
 * @author Losik Yarolsav
 * @version 1.0
 */
public class ViewServlet extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ViewServlet.class);

    private TemplateEngine templateEngine;
    private JavaxServletWebApplication application;

    @Inject
    private SolutionService solutionService;

    @Inject
    private SensorService sensorService;

    @Inject
    private SoundService soundService;

    @Inject
    private AnalysisCommand analysisCommand;

    @Inject
    private RequestService requestService;

    /**
     * Инициализирует сервлет, настраивая шаблонизатор Thymeleaf.
     * Создает резолвер шаблонов с указанием папки, кодировки и других параметров.
     */
    @Override
    public void init() {
        ServletContext servletContext = getServletContext();

        this.application = JavaxServletWebApplication.buildApplication(servletContext);

        WebApplicationTemplateResolver templateResolver =
                new WebApplicationTemplateResolver(this.application);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(false);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        LOG.info("Thymeleaf инициализирован");
    }

    /**
     * Обрабатывает GET-запросы для отображения страниц.
     * В зависимости от пути запроса вызывает соответствующий метод обработки.
     * В случае ошибки формирует детализированную страницу с информацией об исключении.
     *
     * @param req  HttpServletRequest объект, содержащий данные запроса
     * @param resp HttpServletResponse объект для отправки HTML-ответа
     * @throws IOException если происходит ошибка ввода-вывода при записи ответа
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
        String path = req.getServletPath();
        LOG.debug("Обработка GET запроса: {}", path);

        try {
            WebContext ctx = new WebContext(
                    this.application.buildExchange(req, resp),
                    req.getLocale()
            );

            String template;

            if (path.equals("/login")) {
                template = handleLogin(req, ctx);
            } else if (path.equals("/") || path.isEmpty() || path.equals("/dashboard")) {
                LOG.debug("Handling dashboard/home");
                template = handleHome(ctx);
            } else if (path.startsWith("/sensors")) {
                template = handleSensors(req, ctx);
            } else if (path.startsWith("/solutions")) {
                template = handleSolutions(req, ctx);
            } else if (path.equals("/analysis")) {
                template = handleAnalysis(ctx);
            } else if (path.equals("/commands")) {
                template = handleCommands(ctx);
            } else if (path.equals("/dialog")) {
                template = handleDialog(ctx);
            } else if (path.startsWith("/sounds")) {
                template = handleSounds(req, ctx);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOG.debug("Processing template: " + template);
            templateEngine.process(template, ctx, resp.getWriter());

        } catch (Exception e) {
            LOG.error("Ошибка обработки запроса " + path, e);

            resp.setContentType("text/html;charset=UTF-8");
            resp.getWriter().println("<h1>500 Internal Server Error</h1>");
            resp.getWriter().println("<h2>Path: " + path + "</h2>");
            resp.getWriter().println("<pre>");
            e.printStackTrace(new PrintWriter(resp.getWriter()));
            resp.getWriter().println("</pre>");
        }
    }

    /**
     * Обрабатывает DELETE-запросы для удаления сущностей.
     * Удаляет Sensor, Sound или Solution в зависимости от пути запроса.
     * Перенаправляет на соответствующий список после успешного удаления.
     *
     * @param req  HttpServletRequest объект, содержащий путь к удаляемому ресурсу
     * @param resp HttpServletResponse объект для отправки ответа/перенаправления
     * @throws IOException если происходит ошибка ввода-вывода при перенаправлении
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getServletPath();
        LOG.debug("Обработка DELETE запроса: {}", path);

        try {
            String pathInfo = req.getPathInfo();
            String contextPath = req.getContextPath();

            if (pathInfo == null || !pathInfo.contains("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный путь");
                return;
            }

            String[] parts = pathInfo.split("/");
            if (parts.length < 3) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Не указан ID");
                return;
            }

            Long id = Long.parseLong(parts[2]);

            if (path.startsWith("/sensors")) {
                sensorService.deleteSensor(id);
                resp.sendRedirect(contextPath + "/sensors?success=Сенсор удален");
            } else if (path.startsWith("/sounds")) {
                soundService.deleteSound(id);
                resp.sendRedirect(contextPath + "/sounds?success=Звук удален");
            } else if (path.startsWith("/solutions")) {
                solutionService.deleteSolution(id);
                resp.sendRedirect(contextPath + "/solutions?success=Решение удалено");
            }

        } catch (NumberFormatException e) {
            LOG.error("Ошибка преобразования ID", e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID");
        } catch (Exception e) {
            LOG.error("Ошибка удаления", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Обрабатывает отображение страницы входа в систему.
     *
     * @param req объект HttpServletRequest для получения параметров запроса
     * @param ctx контекст Thymeleaf для передачи переменных в шаблон
     * @return имя шаблона для страницы входа
     */
    private String handleLogin(HttpServletRequest req, WebContext ctx) {
        String error = req.getParameter("error");
        ctx.setVariable("hasError", error != null);
        return "login";
    }

    /**
     * Обрабатывает отображение главной страницы / панели управления.
     *
     * @param ctx контекст Thymeleaf для передачи переменных в шаблон
     * @return имя шаблона для главной страницы
     */
    private String handleHome(WebContext ctx) {
        ctx.setVariable("title", "Панель управления");
        ctx.setVariable("totalSensors", sensorService.countAllSensors());
        ctx.setVariable("totalSolutions", solutionService.getTotalSolutionsCount());
        ctx.setVariable("totalRequests", requestService.getTotalRequestsCount());
        return "dashboard";
    }

    /**
     * Обрабатывает отображение страниц, связанных с сенсорами.
     * Включает список, создание, редактирование и просмотр сенсоров.
     *
     * @param req объект HttpServletRequest для определения конкретного действия
     * @param ctx контекст Thymeleaf для передачи данных в шаблон
     * @return имя соответствующего шаблона для сенсоров
     */
    private String handleSensors(HttpServletRequest req, WebContext ctx) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");

            int page = pageParam != null ? Integer.parseInt(pageParam) : 0;
            int size = sizeParam != null ? Integer.parseInt(sizeParam) : 20;

            ctx.setVariable("sensors", sensorService.getAllSensors(page, size));
            ctx.setVariable("totalItems", sensorService.countAllSensors());
            ctx.setVariable("currentPage", page);
            ctx.setVariable("pageSize", size);

            return "sensors/list";

        } else if (pathInfo.startsWith("/edit/")) {
            try {
                Long id = Long.parseLong(pathInfo.substring(6));
                ctx.setVariable("sensor", sensorService.getById(id));
                return "sensors/edit";
            } catch (Exception e) {
                return "error";
            }
        } else if (pathInfo.startsWith("/new")) {
            ctx.setVariable("sensor", new Sensor());
            return "sensors/edit";
        } else if (pathInfo.startsWith("/view/")) {
            try {
                Long id = Long.parseLong(pathInfo.substring(6));
                ctx.setVariable("sensor", sensorService.getById(id));
                return "sensors/view";
            } catch (Exception e) {
                return "error";
            }
        } else {
            return "error";
        }
    }

    /**
     * Обрабатывает отображение страниц, связанных с решениями.
     * Включает список, создание и просмотр решений.
     *
     * @param req объект HttpServletRequest для определения конкретного действия
     * @param ctx контекст Thymeleaf для передачи данных в шаблон
     * @return имя соответствующего шаблона для решений
     */
    private String handleSolutions(HttpServletRequest req, WebContext ctx) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");

            int page = pageParam != null ? Integer.parseInt(pageParam) : 0;
            int size = sizeParam != null ? Integer.parseInt(sizeParam) : 10;

            ctx.setVariable("solutions", solutionService.getAllSolutions(page, size));
            ctx.setVariable("totalItems", solutionService.getTotalSolutionsCount());
            ctx.setVariable("currentPage", page);
            ctx.setVariable("pageSize", size);

            return "solutions/list";

        } else if (pathInfo.startsWith("/new")) {
            ctx.setVariable("solution", new Solution());
            return "solutions/form";

        } else {
            try {
                Long id = Long.parseLong(pathInfo.substring(1));
                ctx.setVariable("solution", solutionService.getById(id));
                return "solutions/view";
            } catch (Exception e) {
                return "error";
            }
        }
    }

    /**
     * Обрабатывает отображение страницы анализа данных.
     *
     * @param ctx контекст Thymeleaf для передачи переменных в шаблон
     * @return имя шаблона для страницы анализа
     */
    private String handleAnalysis(WebContext ctx) {
        ctx.setVariable("title", "Панель анализа");
        ctx.setVariable("totalSolutions", solutionService.getTotalSolutionsCount());
        ctx.setVariable("isAnalysisRunning", false);
        return "analysis/dashboard";
    }

    /**
     * Обрабатывает отображение страницы управления командами.
     *
     * @param ctx контекст Thymeleaf для передачи переменных в шаблон
     * @return имя шаблона для страницы команд
     */
    private String handleCommands(WebContext ctx) {
        ctx.setVariable("title", "Управление командами");
        return "commands/panel";
    }

    /**
     * Обрабатывает отображение страницы диалоговой системы.
     *
     * @param ctx контекст Thymeleaf для передачи переменных в шаблон
     * @return имя шаблона для страницы диалога
     */
    private String handleDialog(WebContext ctx) {
        ctx.setVariable("title", "Диалоговая система");
        return "dialog/panel";
    }

    /**
     * Обрабатывает отображение страниц, связанных со звуками.
     * Включает список, создание, редактирование и просмотр звуков.
     *
     * @param req объект HttpServletRequest для определения конкретного действия
     * @param ctx контекст Thymeleaf для передачи данных в шаблон
     * @return имя соответствующего шаблона для звуков
     */
    private String handleSounds(HttpServletRequest req, WebContext ctx) {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            String pageParam = req.getParameter("page");
            String sizeParam = req.getParameter("size");

            int page = pageParam != null ? Integer.parseInt(pageParam) : 0;
            int size = sizeParam != null ? Integer.parseInt(sizeParam) : 20;

            ctx.setVariable("sounds", soundService.getAllSounds(page, size));
            ctx.setVariable("totalItems", soundService.getTotalSoundsCount());
            ctx.setVariable("currentPage", page);
            ctx.setVariable("pageSize", size);

            return "sounds/list";

        } else if (pathInfo.startsWith("/edit/")) {
            try {
                Long id = Long.parseLong(pathInfo.substring(6));
                ctx.setVariable("sound", soundService.getById(id));
                return "sounds/edit";
            } catch (Exception e) {
                return "error";
            }
        } else if (pathInfo.startsWith("/new")) {
            ctx.setVariable("sound", new Sound());
            return "sounds/edit";
        } else if (pathInfo.startsWith("/view/")) {
            try {
                Long id = Long.parseLong(pathInfo.substring(6));
                ctx.setVariable("sound", soundService.getById(id));
                return "sounds/view";
            } catch (Exception e) {
                return "error";
            }
        } else {
            return "error";
        }
    }

    /**
     * Обрабатывает POST-запросы для создания и обновления сущностей,
     * а также управления анализом и диалоговой системой.
     *
     * @param req  HttpServletRequest объект, содержащий данные формы
     * @param resp HttpServletResponse объект для отправки ответа/перенаправления
     * @throws IOException если происходит ошибка ввода-вывода при перенаправлении
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getServletPath();
        LOG.debug("Обработка POST запроса: {}", path);

        if (path.startsWith("/sensors")) {
            handleSensorPost(req, resp);
        } else if (path.startsWith("/sounds")) {
            handleSoundPost(req, resp);
        } else if (path.startsWith("/solutions")) {
            handleSolutionPost(req, resp);
        } else if (path.equals("/analysis/start")) {
            handleStartAnalysis(req, resp);
        } else if (path.equals("/analysis/cancel")) {
            handleCancelAnalysis(req, resp);
        } else if (path.equals("/dialog/send")) {
            handleSendDialog(req, resp);
        }
    }

    /**
     * Обрабатывает POST-запросы для создания и обновления сенсоров.
     *
     * @param req  объект HttpServletRequest с данными формы сенсора
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleSensorPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String pathInfo = req.getPathInfo();
            String contextPath = req.getContextPath();

            String name = req.getParameter("name");
            String type = req.getParameter("type");
            String location = req.getParameter("location");
            boolean isActive = "on".equals(req.getParameter("active"));

            if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) {
                resp.sendRedirect(contextPath + "/sensors?error=Не заполнены обязательные поля");
                return;
            }

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/new")) {
                Sensor sensor = new Sensor();
                sensor.setName(name.trim());
                sensor.setType(type.trim());
                sensor.setLocation(location != null ? location.trim() : null);
                sensor.setIsActive(isActive);

                Sensor createdSensor = sensorService.createSensor(sensor);
                LOG.info("Создан сенсор с ID: {}", createdSensor.getId());
                resp.sendRedirect(contextPath + "/sensors?success=Сенсор успешно создан");

            } else if (pathInfo.startsWith("/edit/")) {
                String[] parts = pathInfo.split("/");
                if (parts.length > 2) {
                    Long id = Long.parseLong(parts[2]);
                    Sensor existingSensor = sensorService.getById(id);

                    if (existingSensor == null) {
                        resp.sendRedirect(contextPath + "/sensors?error=Сенсор не найден");
                        return;
                    }

                    existingSensor.setName(name.trim());
                    existingSensor.setType(type.trim());
                    existingSensor.setLocation(location != null ? location.trim() : null);
                    existingSensor.setIsActive(isActive);

                    Sensor updatedSensor = sensorService.updateSensor(id, existingSensor);
                    LOG.info("Обновлен сенсор с ID: {}", updatedSensor.getId());
                    resp.sendRedirect(contextPath + "/sensors?success=Сенсор успешно обновлен");
                } else {
                    resp.sendRedirect(contextPath + "/sensors?error=Некорректный ID сенсора");
                }
            } else {
                resp.sendRedirect(contextPath + "/sensors?error=Некорректный путь");
            }

        } catch (NumberFormatException e) {
            LOG.error("Ошибка преобразования ID сенсора", e);
            resp.sendRedirect(req.getContextPath() + "/sensors?error=Некорректный ID сенсора");
        } catch (Exception e) {
            LOG.error("Ошибка сохранения сенсора", e);
            resp.sendRedirect(req.getContextPath() + "/sensors?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }

    /**
     * Обрабатывает POST-запросы для создания и обновления звуков.
     *
     * @param req  объект HttpServletRequest с данными формы звука
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleSoundPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String pathInfo = req.getPathInfo();
            String contextPath = req.getContextPath();

            String noise = req.getParameter("noise");
            String frequencyStr = req.getParameter("frequency");

            if (noise == null || noise.trim().isEmpty() ||
                    frequencyStr == null || frequencyStr.trim().isEmpty()) {
                resp.sendRedirect(contextPath + "/sounds?error=Не заполнены обязательные поля");
                return;
            }

            try {
                int frequency = Integer.parseInt(frequencyStr.trim());

                if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/new")) {
                    Sound sound = new Sound();
                    sound.setNoise(noise.trim());
                    sound.setFrequency(frequency);

                    Sound createdSound = soundService.createSound(sound);
                    LOG.info("Создан звук с ID: {}", createdSound.getId());
                    resp.sendRedirect(contextPath + "/sounds?success=Звук успешно создан");

                } else if (pathInfo.startsWith("/edit/")) {
                    String[] parts = pathInfo.split("/");
                    if (parts.length > 2) {
                        Long id = Long.parseLong(parts[2]);
                        Sound existingSound = soundService.getById(id);

                        if (existingSound == null) {
                            resp.sendRedirect(contextPath + "/sounds?error=Звук не найден");
                            return;
                        }

                        existingSound.setNoise(noise.trim());
                        existingSound.setFrequency(frequency);

                        Sound updatedSound = soundService.updateSound(id, existingSound);
                        LOG.info("Обновлен звук с ID: {}", updatedSound.getId());
                        resp.sendRedirect(contextPath + "/sounds?success=Звук успешно обновлен");
                    } else {
                        resp.sendRedirect(contextPath + "/sounds?error=Некорректный ID звука");
                    }
                } else {
                    resp.sendRedirect(contextPath + "/sounds?error=Некорректный путь");
                }

            } catch (NumberFormatException e) {
                LOG.error("Ошибка преобразования частоты", e);
                resp.sendRedirect(contextPath + "/sounds?error=Частота должна быть целым числом");
            }

        } catch (NumberFormatException e) {
            LOG.error("Ошибка преобразования ID звука", e);
            resp.sendRedirect(req.getContextPath() + "/sounds?error=Некорректный ID звука");
        } catch (Exception e) {
            LOG.error("Ошибка сохранения звука", e);
            resp.sendRedirect(req.getContextPath() + "/sounds?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }

    /**
     * Обрабатывает POST-запросы для создания решений.
     *
     * @param req  объект HttpServletRequest с данными формы решения
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleSolutionPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            String pathInfo = req.getPathInfo();
            String contextPath = req.getContextPath();

            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/new")) {
                Language language = Language.valueOf(req.getParameter("language"));
                String message = req.getParameter("message");

                Solution solution = new Solution();
                solution.setLanguage(language);
                solution.setMessage(message.trim());

                solutionService.createSolution(solution);
                resp.sendRedirect(contextPath + "/solutions?success=Решение успешно создано");
            } else {
                resp.sendRedirect(contextPath + "/solutions?error=Некорректный запрос");
            }

        } catch (Exception e) {
            LOG.error("Ошибка создания решения", e);
            resp.sendRedirect(req.getContextPath() + "/solutions?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }

    /**
     * Обрабатывает запуск анализа данных.
     *
     * @param req  объект HttpServletRequest
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleStartAnalysis(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            analysisCommand.call();
            resp.sendRedirect(req.getContextPath() + "/analysis?status=Анализ запущен");
        } catch (Exception e) {
            LOG.error("Ошибка запуска анализа", e);
            resp.sendRedirect(req.getContextPath() + "/analysis?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }

    /**
     * Обрабатывает отмену анализа данных.
     *
     * @param req  объект HttpServletRequest
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleCancelAnalysis(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            analysisCommand.cancel();
            resp.sendRedirect(req.getContextPath() + "/analysis?status=Анализ отменен");
        } catch (Exception e) {
            LOG.error("Ошибка отмены анализа", e);
            resp.sendRedirect(req.getContextPath() + "/analysis?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }

    /**
     * Обрабатывает отправку сообщения в диалоговой системе.
     *
     * @param req  объект HttpServletRequest с текстом сообщения
     * @param resp объект HttpServletResponse для перенаправления
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleSendDialog(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            String message = req.getParameter("message");
            if (message == null || message.trim().isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/dialog?error=Сообщение не может быть пустым");
                return;
            }

            // Здесь можно добавить логику обработки диалога
            String response = "Обработано: " + message.trim();
            resp.sendRedirect(req.getContextPath() + "/dialog?response=" +
                    response.replace(" ", "%20"));
        } catch (Exception e) {
            LOG.error("Ошибка отправки сообщения", e);
            resp.sendRedirect(req.getContextPath() + "/dialog?error=" +
                    e.getMessage().replace(" ", "%20"));
        }
    }
}