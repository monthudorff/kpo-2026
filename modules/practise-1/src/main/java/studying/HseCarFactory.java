package studying;

import java.util.ArrayList;
import java.util.List;

/**
 * Предприятие автотранспортного факультета НИУ ВШЭ.
 * Автомобили фабрика производит сама, вне её они не существуют (композиция).
 * Покупатели приходят снаружи и существуют независимо от фабрики (агрегация).
 */
public class HseCarFactory {
    private final List<Car> cars = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private int lastSerialNumber = 0;

    /** Производит новый автомобиль с двигателем заданного размера педалей и ставит его на склад. */
    public void addCar(int pedalSize) {
        lastSerialNumber++;
        cars.add(new Car(lastSerialNumber, pedalSize));
    }

    /** Ставит покупателя в очередь. */
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    /**
     * Проходит по очереди покупателей и вручает каждому, у кого ещё нет автомобиля,
     * первый автомобиль со склада. Врученный автомобиль со склада исчезает.
     * Если всем желающим выдали автомобиль, а на складе что-то осталось, остатки ликвидируются.
     */
    public void saleCar() {
        for (Customer customer : customers) {
            if (customer.hasCar()) {
                continue;
            }
            if (cars.isEmpty()) {
                break;
            }
            Car car = cars.removeFirst();
            customer.setCar(car);
        }

        if (allCustomersHaveCars() && !cars.isEmpty()) {
            cars.clear();
        }
    }

    public void printCars() {
        if (cars.isEmpty()) {
            System.out.println("Склад пуст");
            return;
        }
        for (Car car : cars) {
            System.out.println(car);
        }
    }

    public void printCustomers() {
        if (customers.isEmpty()) {
            System.out.println("Очередь пуста");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    private boolean allCustomersHaveCars() {
        for (Customer customer : customers) {
            if (!customer.hasCar()) {
                return false;
            }
        }
        return true;
    }
}
