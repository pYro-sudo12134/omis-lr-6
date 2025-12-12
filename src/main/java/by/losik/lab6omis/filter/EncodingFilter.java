package by.losik.lab6omis.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Фильтр кодировки для установки UTF-8 кодировки всем входящим и исходящим данным.
 * Гарантирует корректную обработку кириллических и других Unicode символов
 * в запросах и ответах веб-приложения.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class EncodingFilter implements Filter {

    /**
     * Устанавливает кодировку UTF-8 для запроса и ответа.
     * Настраивает тип контента ответа как HTML с кодировкой UTF-8.
     * Передает запрос и ответ дальше по цепочке фильтров.
     *
     * @param request  ServletRequest объект входящего запроса
     * @param response ServletResponse объект для ответа
     * @param chain    FilterChain для передачи запроса следующему фильтру или сервлету
     * @throws IOException      если происходит ошибка ввода-вывода
     * @throws ServletException если происходит ошибка сервлета
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        chain.doFilter(request, response);
    }
}