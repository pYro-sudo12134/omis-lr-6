package by.losik.lab6omis.service.base;

import java.util.List;

/**
 * Интерфейс стратегии анализа данных.
 * Определяет контракт для различных алгоритмов анализа.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public interface AnalysisStrategy {
    /**
     * Выполняет анализ предоставленных данных.
     *
     * @param data список данных для анализа
     */
    void analyze(List<String> data);
}