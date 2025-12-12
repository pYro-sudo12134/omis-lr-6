package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.service.base.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Команда для работы с решениями.
 * Реализует интерфейс Command.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class SolutionCommand implements Command {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Выполняет команду обработки решений.
     */
    @Override
    public void call() {
        LOG.info(String.format("%s был вызван!", getClass()));
    }

    /**
     * Отменяет выполнение команды обработки решений.
     */
    @Override
    public void cancel() {
        LOG.info(String.format("%s был отменен!", getClass()));
    }
}