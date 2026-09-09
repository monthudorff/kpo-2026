package studying;

/**
 * Педальный автомобиль.
 * Двигатель создаёт сам и наружу не отдаёт (композиция),
 * поэтому автомобиля без двигателя не бывает.
 */
public class Car {
    private final int serialNumber;
    private final Engine engine;

    public Car(int serialNumber, int pedalSize) {
        this.serialNumber = serialNumber;
        this.engine = new Engine(pedalSize);
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public int getPedalSize() {
        return engine.getPedalSize();
    }

    @Override
    public String toString() {
        return "автомобиль №" + serialNumber + " (" + engine + ")";
    }
}
