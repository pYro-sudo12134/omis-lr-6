package by.losik.lab6omis.service.base;

/**
 * Интерфейс команды для реализации паттерна "Команда".
 * Определяет операции выполнения и отмены команды.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public interface Command {
    /**
     * Выполняет команду.
     */
    void call();

    /**
     * Отменяет выполнение команды.
     */
    void cancel();
}