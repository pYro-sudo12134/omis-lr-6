package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.service.base.AnalysisStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Стратегия анализа данных с использованием машинного обучения.
 * Реализует интерфейс AnalysisStrategy.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class MLStrategy implements AnalysisStrategy {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Выполняет анализ данных с использованием ML-методов.
     *
     * @param data список данных для анализа
     */
    @Override
    public void analyze(List<String> data) {
        LOG.info(String.format("%s был вызван!", getClass()));
    }
}