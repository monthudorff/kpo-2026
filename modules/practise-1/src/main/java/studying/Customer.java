package studying;

/**
 * Покупатель.
 * Создаётся снаружи и живёт независимо от фабрики и от автомобиля (агрегация).
 * Пока автомобиль не куплен, поле car равно null.
 */
public class Customer {
    private final String fullName;
    private Car car;

    public Customer(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public Car getCar() {
        return car;
    }

    public boolean hasCar() {
        return car != null;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        if (car == null) {
            return fullName + ": без автомобиля";
        }
        return fullName + ": " + car;
    }
}
