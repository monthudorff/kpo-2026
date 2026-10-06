package ru.hse.vyshkat.console;

/**
 * Поток ввода закончился: Ctrl+D или конец демо-сценария.
 */
public final class InputClosedException extends RuntimeException {

    /**
     * Создаёт исключение о завершении ввода.
     */
    public InputClosedException() {
        super("ввод завершён");
    }
}
