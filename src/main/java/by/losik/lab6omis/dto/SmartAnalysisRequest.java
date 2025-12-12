package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

import java.util.List;

/**
 * DTO для интеллектуального анализа.
 */
public class SmartAnalysisRequest {
    private List<String> data;
    private String dataType;
    private int dataSize;
    private Language language;

    public List<String> getData() { return data; }
    public void setData(List<String> data) { this.data = data; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public int getDataSize() { return dataSize; }
    public void setDataSize(int dataSize) { this.dataSize = dataSize; }

    public Language getLanguage() { return language; }
    public void setLanguage(Language language) { this.language = language; }
}