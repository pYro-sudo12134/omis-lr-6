package by.losik.lab6omis.controller;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.service.general.types.SolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ViewController", urlPatterns = {"", "/", "/solutions", "/solutions/*", "/analysis", "/commands"})
public class ViewController extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(ViewController.class);

    private TemplateEngine templateEngine;
    private JavaxServletWebApplication application;

    @Inject
    private SolutionService solutionService;

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
        templateResolver.setCacheable(false); // Для разработки

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);

        LOG.info("Thymeleaf инициализирован");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String path = req.getServletPath();
        LOG.debug("Обработка GET запроса: {}", path);

        WebContext ctx = new WebContext(
                this.application.buildExchange(req, resp),
                req.getLocale()
        );

        try {
            String template;

            if (path.equals("/") || path.isEmpty()) {
                template = handleHome(ctx);
            } else if (path.startsWith("/solutions")) {
                template = handleSolutions(req, ctx);
            } else if (path.equals("/analysis")) {
                template = handleAnalysis(ctx);
            } else if (path.equals("/commands")) {
                template = handleCommands(ctx);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            templateEngine.process(template, ctx, resp.getWriter());

        } catch (Exception e) {
            LOG.error("Ошибка обработки запроса", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String handleHome(WebContext ctx) {
        ctx.setVariable("title", "Система анализа данных");
        ctx.setVariable("welcome", "Добро пожаловать в систему!");
        return "index";
    }

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

    private String handleAnalysis(WebContext ctx) {
        ctx.setVariable("title", "Панель анализа");
        ctx.setVariable("totalSolutions", solutionService.getTotalSolutionsCount());
        return "analysis/dashboard";
    }

    private String handleCommands(WebContext ctx) {
        ctx.setVariable("title", "Управление командами");
        return "commands/panel";
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        if (req.getServletPath().equals("/solutions")) {
            handleCreateSolution(req, resp);
        }
    }

    private void handleCreateSolution(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        try {
            Solution solution = new Solution();
            solution.setLanguage(Language.valueOf(req.getParameter("language")));
            solution.setMessage(req.getParameter("message"));

            solutionService.createSolution(solution);

            resp.sendRedirect(req.getContextPath() + "/solutions");

        } catch (Exception e) {
            LOG.error("Ошибка создания решения", e);
            resp.sendRedirect(req.getContextPath() + "/solutions/new?error=" +
                    e.getMessage());
        }
    }
}