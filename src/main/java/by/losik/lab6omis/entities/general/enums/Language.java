package by.losik.lab6omis.entities.general.enums;

/**
 * Перечисление языков, поддерживаемых в системе.
 * Каждый язык имеет код (название перечисления) и отображаемое имя на русском языке.
 *
 * <p>Поддерживаемые языки:</p>
 * <ul>
 *   <li>RU - Русский</li>
 *   <li>EN - Английский</li>
 *   <li>DE - Немецкий</li>
 *   <li>FR - Французский</li>
 *   <li>ES - Испанский</li>
 *   <li>ZH - Китайский</li>
 * </ul>
 *
 * <p>Пример использования:</p>
 * <pre>
 * Language language = Language.EN;
 * System.out.println(language.getDisplayName()); // "Английский"
 * </pre>
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public enum Language {

    /**
     * Русский язык
     */
    RU("Русский"),

    /**
     * Английский язык
     */
    EN("Английский"),

    /**
     * Немецкий язык
     */
    DE("Немецкий"),

    /**
     * Французский язык
     */
    FR("Французский"),

    /**
     * Испанский язык
     */
    ES("Испанский"),

    /**
     * Китайский язык
     */
    ZH("Китайский");

    /**
     * Отображаемое имя языка на русском
     */
    private final String displayName;

    /**
     * Создает экземпляр языка с указанным отображаемым именем.
     *
     * @param displayName отображаемое имя языка на русском
     */
    Language(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Возвращает отображаемое имя языка на русском.
     *
     * @return отображаемое имя языка
     */
    public String getDisplayName() {
        return displayName;
    }
}