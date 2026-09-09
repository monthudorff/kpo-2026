package studying;

/**
 * Педальный двигатель.
 * Существует только внутри автомобиля: его создаёт сам Car (композиция).
 */
public class Engine {
    private final int pedalSize;

    public Engine(int pedalSize) {
        this.pedalSize = pedalSize;
    }

    public int getPedalSize() {
        return pedalSize;
    }

    @Override
    public String toString() {
        return "педальный двигатель, размер педалей " + pedalSize;
    }
}
