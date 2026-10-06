package ru.hse.vyshkat.domain;

/**
 * Объект, который стоит на балансе «ВышКат» и имеет инвентарный номер.
 */
public interface Inventoried {

    /**
     * Возвращает инвентарный номер, уникальный в пределах баланса.
     *
     * @return инвентарный номер
     */
    int getInventoryNumber();

    /**
     * Возвращает наименование для инвентарной ведомости.
     *
     * @return наименование
     */
    String getName();
}
