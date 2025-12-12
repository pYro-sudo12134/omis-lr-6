package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.types.Sound;
import by.losik.lab6omis.repository.general.types.SoundRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для управления звуками (Sound).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 * Использует {@link BaseService} для общей логики работы с сущностями.
 *
 * @see Sound
 * @see SoundRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class SoundService extends BaseService<Sound, Long> {

    @Inject
    SoundRepository soundRepository;
    private static final int MIN_FREQUENCY = 1;
    private static final int MAX_FREQUENCY = 200000;

    /**
     * Создает новый звук в системе.
     *
     * @param sound объект звука для создания (должен быть валидным)
     * @return сохраненный объект звука с присвоенным ID
     * @throws IllegalArgumentException если звук с таким типом шума уже существует
     *                                  или параметры частоты некорректны
     */
    public Sound createSound(@Valid Sound sound) {
        return executeWithLogging(
                String.format("Создание звука: тип шума='%s', частота=%d Гц",
                        sound.getNoise(), sound.getFrequency()),
                () -> {
                    validateSound(sound);
                    validateUnique(sound.getNoise(),
                            () -> soundRepository.existsByNoise(sound.getNoise()),
                            "типом шума", "Звук");

                    return soundRepository.create(sound);
                }
        );
    }

    /**
     * Получает звук по его идентификатору.
     *
     * @param id идентификатор звука
     * @return найденный объект звука
     * @throws NotFoundException если звук с указанным ID не найден
     */
    public Sound getById(Long id) {
        return getEntityById(
                id,
                () -> soundRepository.findById(id),
                "Звук"
        );
    }

    /**
     * Получает все звуки из системы.
     *
     * @return список всех звуков
     */
    public List<Sound> getAllSounds() {
        return executeWithLogging(
                "Получение всех звуков",
                soundRepository::findAll
        );
    }

    /**
     * Получает звуки с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return список звуков на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Sound> getAllSounds(int page, int size) {
        return executeWithLogging(
                String.format("Получение звуков с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return soundRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующий звук.
     *
     * @param id идентификатор обновляемого звука
     * @param updatedSound обновленные данные звука (должны быть валидными)
     * @return обновленный объект звука
     * @throws NotFoundException если звук с указанным ID не найден
     * @throws IllegalArgumentException если новый тип шума уже существует у другого звука
     *                                  или частота некорректна
     */
    public Sound updateSound(Long id, @Valid Sound updatedSound) {
        return executeWithLogging(
                String.format("Обновление звука ID %d", id),
                () -> {
                    Sound existingSound = getById(id);
                    validateSound(updatedSound);

                    if (!existingSound.getNoise().equals(updatedSound.getNoise())) {
                        validateUnique(updatedSound.getNoise(),
                                () -> soundRepository.existsByNoise(updatedSound.getNoise()),
                                "типом шума", "Звук");
                    }

                    existingSound.setNoise(updatedSound.getNoise());
                    existingSound.setFrequency(updatedSound.getFrequency());

                    return soundRepository.save(existingSound);
                }
        );
    }

    /**
     * Удаляет звук по его идентификатору.
     *
     * @param id идентификатор удаляемого звука
     * @throws NotFoundException если звук с указанным ID не найден
     */
    public void deleteSound(Long id) {
        executeVoidWithLogging(
                String.format("Удаление звука ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> soundRepository.existsById(id),
                            "Звук");
                    soundRepository.deleteById(id);
                }
        );
    }

    /**
     * Ищет звуки по частичному совпадению типа шума.
     *
     * @param searchText текст для поиска в типе шума
     * @return список найденных звуков
     */
    public List<Sound> searchByNoise(String searchText) {
        return executeWithLogging(
                String.format("Поиск звуков по типу шума: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return soundRepository.findByNoiseContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает звуки с частотой в заданном диапазоне.
     *
     * @param minFrequency минимальная частота (Гц)
     * @param maxFrequency максимальная частота (Гц)
     * @return список звуков с частотой в указанном диапазоне
     * @throws IllegalArgumentException если параметры частоты некорректны
     */
    public List<Sound> getByFrequencyBetween(Integer minFrequency, Integer maxFrequency) {
        return executeWithLogging(
                String.format("Поиск звуков с частотой от %d до %d Гц", minFrequency, maxFrequency),
                () -> {
                    validateFrequencyRange(minFrequency, maxFrequency);
                    return soundRepository.findByFrequencyBetween(minFrequency, maxFrequency);
                }
        );
    }

    /**
     * Получает звуки с низкой частотой (ниже указанного значения).
     *
     * @param maxFrequency максимальная частота (Гц)
     * @return список звуков с низкой частотой
     * @throws IllegalArgumentException если параметр частоты некорректен
     */
    public List<Sound> getLowFrequencySounds(Integer maxFrequency) {
        return executeWithLogging(
                String.format("Поиск звуков с частотой ниже %d Гц", maxFrequency),
                () -> {
                    validateFrequencyValue(maxFrequency);
                    return soundRepository.findByLowFrequency(maxFrequency);
                }
        );
    }

    /**
     * Получает звуки с высокой частотой (выше указанного значения).
     *
     * @param minFrequency минимальная частота (Гц)
     * @return список звуков с высокой частотой
     * @throws IllegalArgumentException если параметр частоты некорректен
     */
    public List<Sound> getHighFrequencySounds(Integer minFrequency) {
        return executeWithLogging(
                String.format("Поиск звуков с частотой выше %d Гц", minFrequency),
                () -> {
                    validateFrequencyValue(minFrequency);
                    return soundRepository.findByHighFrequency(minFrequency);
                }
        );
    }

    /**
     * Получает звуки по точному совпадению типа шума.
     *
     * @param noise точный тип шума
     * @return Optional содержащий найденный звук или пустой
     */
    public Optional<Sound> getByNoise(String noise) {
        return executeWithLogging(
                String.format("Поиск звука по точному типу шума: '%s'", noise),
                () -> {
                    if (noise == null || noise.trim().isEmpty()) {
                        return Optional.empty();
                    }
                    return soundRepository.findByNoise(noise.trim());
                }
        );
    }

    /**
     * Получает звуки по точной частоте.
     *
     * @param frequency точная частота (Гц)
     * @return список звуков с указанной частотой
     * @throws IllegalArgumentException если параметр частоты некорректен
     */
    public List<Sound> getByFrequency(Integer frequency) {
        return executeWithLogging(
                String.format("Поиск звуков с частотой %d Гц", frequency),
                () -> {
                    validateFrequencyValue(frequency);
                    return soundRepository.findByFrequency(frequency);
                }
        );
    }

    /**
     * Получает статистику по средним частотам для каждого типа шума.
     *
     * @return карта, где ключ - тип шума, значение - средняя частота
     */
    public Map<String, Double> getAverageFrequencyByNoiseType() {
        return executeWithLogging(
                "Получение статистики средних частот по типам шума",
                soundRepository::getAverageFrequencyByNoiseType
        );
    }

    /**
     * Рассчитывает среднюю частоту всех звуков.
     *
     * @return средняя частота (Гц)
     */
    public Double getAverageFrequency() {
        return executeWithLogging(
                "Расчет средней частоты всех звуков",
                () -> {
                    Double avgFrequency = soundRepository.getAverageFrequency();
                    return avgFrequency != null ? avgFrequency : 0.0;
                }
        );
    }

    /**
     * Получает звуки с минимальной частотой.
     *
     * @return список звуков с минимальной частотой
     */
    public List<Sound> getLowestFrequencySounds() {
        return executeWithLogging(
                "Поиск звуков с минимальной частотой",
                soundRepository::findLowestFrequencySounds
        );
    }

    /**
     * Получает звуки с максимальной частотой.
     *
     * @return список звуков с максимальной частотой
     */
    public List<Sound> getHighestFrequencySounds() {
        return executeWithLogging(
                "Поиск звуков с максимальной частотой",
                soundRepository::findHighestFrequencySounds
        );
    }

    /**
     * Проверяет существование звука с указанным типом шума.
     *
     * @param noise тип шума для проверки
     * @return true если звук существует, false в противном случае
     */
    public boolean existsByNoise(String noise) {
        return executeWithLogging(
                String.format("Проверка существования звука с типом шума: '%s'", noise),
                () -> {
                    if (noise == null || noise.trim().isEmpty()) {
                        return false;
                    }
                    return soundRepository.existsByNoise(noise.trim());
                }
        );
    }

    /**
     * Удаляет все звуки с указанной частотой.
     *
     * @param frequency частота (Гц)
     * @return количество удаленных звуков
     * @throws IllegalArgumentException если частота некорректна
     */
    public int deleteByFrequency(Integer frequency) {
        return executeWithLogging(
                String.format("Удаление всех звуков с частотой %d Гц", frequency),
                () -> {
                    validateFrequencyValue(frequency);
                    return soundRepository.deleteByFrequency(frequency);
                }
        );
    }

    /**
     * Получает общее количество звуков в системе.
     *
     * @return общее количество звуков
     */
    public long getTotalSoundsCount() {
        return executeWithLogging(
                "Получение общего количества звуков",
                soundRepository::count
        );
    }

    /**
     * Получает количество звуков с указанной частотой.
     *
     * @param frequency частота для подсчета
     * @return количество звуков с указанной частотой
     * @throws IllegalArgumentException если частота некорректна
     */
    public Long countByFrequency(Integer frequency) {
        return executeWithLogging(
                String.format("Подсчет количества звуков с частотой %d Гц", frequency),
                () -> {
                    validateFrequencyValue(frequency);
                    Long count = soundRepository.countByFrequency(frequency);
                    return count != null ? count : 0L;
                }
        );
    }

    /**
     * Получает статистику по количеству звуков в разных частотных диапазонах.
     *
     * @param rangeSize размер диапазона (например, 1000 для диапазонов 0-1000, 1001-2000 и т.д.)
     * @return карта, где ключ - диапазон, значение - количество звуков
     * @throws IllegalArgumentException если размер диапазона некорректен
     */
    public Map<String, Long> getSoundCountByFrequencyRange(int rangeSize) {
        return executeWithLogging(
                String.format("Получение статистики звуков по частотным диапазонам, размер диапазона: %d", rangeSize),
                () -> {
                    if (rangeSize <= 0) {
                        throw new IllegalArgumentException("Размер диапазона должен быть положительным");
                    }
                    return soundRepository.getSoundCountByFrequencyRange(rangeSize);
                }
        );
    }

    /**
     * Получает звуки по типу шума с сортировкой по частоте.
     *
     * @param noise тип шума (может быть частью)
     * @param ascending true - по возрастанию, false - по убыванию
     * @return отсортированный список звуков
     */
    public List<Sound> getByNoiseOrderByFrequency(String noise, boolean ascending) {
        return executeWithLogging(
                String.format("Поиск звуков по типу шума '%s' с сортировкой по частоте (ascending=%s)", noise, ascending),
                () -> {
                    if (noise == null || noise.trim().isEmpty()) {
                        return List.of();
                    }
                    return ascending
                            ? soundRepository.findByNoiseOrderByFrequencyAsc(noise.trim())
                            : soundRepository.findByNoiseOrderByFrequencyDesc(noise.trim());
                }
        );
    }

    /**
     * Получает звуки, тип шума которых начинается с указанного префикса.
     *
     * @param prefix префикс типа шума
     * @return список звуков, чьи типы шума начинаются с указанного префикса
     */
    public List<Sound> getByNoiseStartingWith(String prefix) {
        return executeWithLogging(
                String.format("Поиск звуков по префиксу типа шума: '%s'", prefix),
                () -> {
                    if (prefix == null || prefix.trim().isEmpty()) {
                        return List.of();
                    }
                    return soundRepository.findByNoiseStartingWith(prefix.trim());
                }
        );
    }

    /**
     * Получает звуки, тип шума которых заканчивается на указанный суффикс.
     *
     * @param suffix суффикс типа шума
     * @return список звуков, чьи типы шума заканчиваются на указанный суффикс
     */
    public List<Sound> getByNoiseEndingWith(String suffix) {
        return executeWithLogging(
                String.format("Поиск звуков по суффиксу типа шума: '%s'", suffix),
                () -> {
                    if (suffix == null || suffix.trim().isEmpty()) {
                        return List.of();
                    }
                    return soundRepository.findByNoiseEndingWith(suffix.trim());
                }
        );
    }

    /**
     * Получает звуки по нескольким типам шума.
     *
     * @param noises список типов шума
     * @return список звуков с указанными типами шума
     * @throws IllegalArgumentException если список типов шума пуст
     */
    public List<Sound> getByNoises(List<String> noises) {
        return executeWithLogging(
                String.format("Поиск звуков по типам шума: %s", noises),
                () -> {
                    List<String> cleanedNoises = cleanStringList(noises);
                    if (cleanedNoises.isEmpty()) {
                        throw new IllegalArgumentException("Список типов шума не может быть пустым");
                    }
                    return soundRepository.findByNoises(cleanedNoises);
                }
        );
    }

    /**
     * Получает звуки с частотой в нескольких диапазонах.
     *
     * @param frequencyRanges список диапазонов [min, max]
     * @return список звуков, попадающих в указанные диапазоны
     * @throws IllegalArgumentException если список диапазонов пуст или некорректен
     */
    public List<Sound> getByFrequencyRanges(List<int[]> frequencyRanges) {
        return executeWithLogging(
                String.format("Поиск звуков по %d частотным диапазонам", frequencyRanges != null ? frequencyRanges.size() : 0),
                () -> {
                    if (frequencyRanges == null || frequencyRanges.isEmpty()) {
                        throw new IllegalArgumentException("Список диапазонов частот не может быть пустым");
                    }

                    // Проверяем каждый диапазон
                    for (int[] range : frequencyRanges) {
                        if (range.length != 2) {
                            throw new IllegalArgumentException("Каждый диапазон должен содержать 2 значения [min, max]");
                        }
                        validateFrequencyRange(range[0], range[1]);
                    }

                    return soundRepository.findByFrequencyRanges(frequencyRanges);
                }
        );
    }

    /**
     * Получает звуки по частоте с пагинацией.
     *
     * @param frequency частота (Гц)
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список звуков для указанной страницы
     * @throws IllegalArgumentException если параметры пагинации или частоты некорректны
     */
    public List<Sound> getByFrequencyWithPagination(Integer frequency, int page, int size) {
        return executeWithLogging(
                String.format("Поиск звуков с частотой %d Гц с пагинацией: page=%d, size=%d", frequency, page, size),
                () -> {
                    validateFrequencyValue(frequency);
                    validatePagination(page, size);
                    return soundRepository.findByFrequencyWithPagination(frequency, page, size);
                }
        );
    }

    /**
     * Проверяет, является ли звук новым (не сохраненным в БД).
     *
     * @param sound объект звука
     * @return true если звук новый, false если существует в БД
     */
    public boolean isNewSound(Sound sound) {
        return executeWithLogging(
                "Проверка, является ли звук новым",
                () -> soundRepository.isNew(sound)
        );
    }

    /**
     * Валидирует объект звука.
     *
     * @param sound объект звука для валидации
     * @throws IllegalArgumentException если звук не соответствует требованиям
     */
    private void validateSound(Sound sound) {
        validateNotNull(sound, "Звук");
        validateNotNull(sound.getFrequency(), "Частота звука");
        validateStringLength(sound.getNoise(), "Тип шума", 2, 100);
        validateFrequency(sound.getFrequency());
    }

    /**
     * Валидирует значение частоты звука.
     *
     * @param frequency частота для валидации (Гц)
     * @throws IllegalArgumentException если частота некорректна
     */
    private void validateFrequency(Integer frequency) {
        validateNotNull(frequency, "Частота");
        validateNumberRange(frequency, "Частота", MIN_FREQUENCY, MAX_FREQUENCY);
    }

    /**
     * Валидирует общее значение частоты.
     *
     * @param frequency частота для валидации
     * @throws IllegalArgumentException если частота некорректна
     */
    private void validateFrequencyValue(Integer frequency) {
        validateNotNull(frequency, "Значение частоты");
        if (frequency < 0) {
            throw new IllegalArgumentException("Значение частоты не может быть отрицательным");
        }
    }

    /**
     * Валидирует диапазон частот.
     *
     * @param minFrequency минимальная частота
     * @param maxFrequency максимальная частота
     * @throws IllegalArgumentException если диапазон некорректен
     */
    private void validateFrequencyRange(Integer minFrequency, Integer maxFrequency) {
        validateNotNull(minFrequency, "Минимальная частота");
        validateNotNull(maxFrequency, "Максимальная частота");

        if (minFrequency < 0 || maxFrequency < 0) {
            throw new IllegalArgumentException("Частоты не могут быть отрицательными");
        }

        if (minFrequency > maxFrequency) {
            throw new IllegalArgumentException(
                    String.format("Минимальная частота (%d) не может быть больше максимальной (%d)",
                            minFrequency, maxFrequency)
            );
        }
    }
}