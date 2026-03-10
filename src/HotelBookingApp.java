abstract class Room {
    private String name;
    private int beds;
    private double pricePerNight;

    public Room(String name, int beds, double pricePerNight) {
        this.name = name;
        this.beds = beds;
        this.pricePerNight = pricePerNight;
    }

    public String getName() {
        return name;
    }

    public int getBeds() {
        return beds;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public abstract void displayInfo();
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 50.0);
    }

    @Override
    public void displayInfo() {
        System.out.println(getName() + " - Beds: " + getBeds() + ", Price: $" + getPricePerNight());
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 90.0);
    }

    @Override
    public void displayInfo() {
        System.out.println(getName() + " - Beds: " + getBeds() + ", Price: $" + getPricePerNight());
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 150.0);
    }

    @Override
    public void displayInfo() {
        System.out.println(getName() + " - Beds: " + getBeds() + ", Price: $" + getPricePerNight());
    }
}

public class HotelBookingApp{
    public static void main(String[] args) {

        int singleRoomAvailable = 5;
        int doubleRoomAvailable = 3;
        int suiteRoomAvailable = 2;

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        System.out.println("===== Room Information =====");
        single.displayInfo();
        System.out.println("Availability: " + singleRoomAvailable);

        doubleRoom.displayInfo();
        System.out.println("Availability: " + doubleRoomAvailable);

        suite.displayInfo();
        System.out.println("Availability: " + suiteRoomAvailable);

        System.out.println("============================");
        System.out.println("Application terminated.");
    }
}