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

    public Map<String, Integer> getAllAvailability() {
        return new HashMap<>(inventory);
    }
}

class RoomSearchService {
    private RoomInventory inventory;

    public RoomSearchService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void searchAvailableRooms(Room[] rooms) {
        System.out.println("===== Available Rooms =====");
        for (Room room : rooms) {
            int available = inventory.getAvailability(room.getName());
            if (available > 0) {
                room.displayInfo();
                System.out.println("Availability: " + available);
            }
        }
        System.out.println("===========================");
    }
}

public class HotelBookingApp{
    public static void main(String[] args) {
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoom("Single Room", 5);
        inventory.registerRoom("Double Room", 3);
        inventory.registerRoom("Suite Room", 0);

        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        RoomSearchService searchService = new RoomSearchService(inventory);
        searchService.searchAvailableRooms(rooms);

        System.out.println("Search operation completed. Inventory unchanged.");
    }
}