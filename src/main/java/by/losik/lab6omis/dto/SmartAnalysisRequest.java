package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;
import java.util.List;

/**
 * DTO для интеллектуального анализа.
 * Используется для передачи параметров анализа данных в REST запросах.
 */
public class SmartAnalysisRequest {
    private List<String> data;
    private String dataType;
    private int dataSize;
    private Language language;

    /**
     * Возвращает список данных для анализа.
     *
     * @return список строк с данными
     */
    public List<String> getData() {
        return data;
    }

    /**
     * Устанавливает список данных для анализа.
     *
     * @param data список строк с данными
     */
    public void setData(List<String> data) {
        this.data = data;
    }

    /**
     * Возвращает тип данных для анализа.
     *
     * @return тип данных
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Устанавливает тип данных для анализа.
     *
     * @param dataType тип данных
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Возвращает размер данных для анализа.
     *
     * @return размер данных
     */
    public int getDataSize() {
        return dataSize;
    }

    /**
     * Устанавливает размер данных для анализа.
     *
     * @param dataSize размер данных
     */
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    /**
     * Возвращает язык данных для анализа.
     *
     * @return язык данных
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Устанавливает язык данных для анализа.
     *
     * @param language язык данных
     */
    public void setLanguage(Language language) {
        this.language = language;
    }
}