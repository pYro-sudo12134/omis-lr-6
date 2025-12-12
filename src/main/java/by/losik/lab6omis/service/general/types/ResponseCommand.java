package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.service.base.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Команда для формирования ответов.
 * Реализует интерфейс Command.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class ResponseCommand implements Command {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Выполняет команду формирования ответа.
     */
    @Override
    public void call() {
        LOG.info(String.format("%s был вызван!", getClass()));
    }

    /**
     * Отменяет выполнение команды формирования ответа.
     */
    @Override
    public void cancel() {
        LOG.info(String.format("%s был отменен!", getClass()));
    }
}