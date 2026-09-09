package studying;

public class Main {
    public static void main(String[] args) {
        var factory = new HseCarFactory();

        factory.addCar(1);
        factory.addCar(2);
        factory.addCar(3);
        factory.addCar(4);
        factory.addCustomer(new Customer("Вася"));
        factory.addCustomer(new Customer("Вова"));
        factory.addCustomer(new Customer("Света"));

        System.out.println("== Автомобили до продажи ==");
        factory.printCars();
        System.out.println("== Покупатели до продажи ==");
        factory.printCustomers();

        factory.saleCar();

        System.out.println("== Автомобили после продажи ==");
        factory.printCars();
        System.out.println("== Покупатели после продажи ==");
        factory.printCustomers();
    }
}
