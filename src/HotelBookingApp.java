import java.util.HashMap;
import java.util.Map;

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

class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    public void registerRoom(String roomType, int count) {
        inventory.put(roomType, count);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public void updateAvailability(String roomType, int delta) {
        int current = inventory.getOrDefault(roomType, 0);
        int updated = current + delta;
        if (updated < 0) updated = 0;
        inventory.put(roomType, updated);
    }

    public void displayInventory() {
        System.out.println("===== Room Inventory =====");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " - Available: " + entry.getValue());
        }
        System.out.println("==========================");
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoom("Single Room", 5);
        inventory.registerRoom("Double Room", 3);
        inventory.registerRoom("Suite Room", 2);

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        single.displayInfo();
        System.out.println("Availability: " + inventory.getAvailability(single.getName()));

        doubleRoom.displayInfo();
        System.out.println("Availability: " + inventory.getAvailability(doubleRoom.getName()));

        suite.displayInfo();
        System.out.println("Availability: " + inventory.getAvailability(suite.getName()));

        inventory.updateAvailability("Single Room", -1);
        inventory.updateAvailability("Suite Room", 1);

        inventory.displayInventory();

        System.out.println("Application terminated.");
    }
}