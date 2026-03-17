import java.io.*;
import java.util.*;
import java.util.concurrent.*;

class Reservation implements Serializable {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room Type: " + roomType + ", Room ID: " + roomId);
    }
}

class BookingRequestQueue implements Serializable {
    private Queue<Reservation> queue = new LinkedList<>();
    public void addRequest(Reservation r) { queue.add(r); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean hasRequests() { return !queue.isEmpty(); }
}

class InventoryService implements Serializable {
    private Map<String, Integer> inventory = new HashMap<>();
    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }
    public boolean isAvailable(String roomType) { return inventory.getOrDefault(roomType, 0) > 0; }
    public void decrement(String roomType) { inventory.put(roomType, inventory.get(roomType) - 1); }
    public void increment(String roomType) { inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1); }
    public void displayInventory() { System.out.println(inventory); }
}

class BookingService implements Serializable {
    private InventoryService inventoryService;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomAllocationMap = new HashMap<>();
    private List<Reservation> bookingHistory = new ArrayList<>();

    public BookingService(InventoryService inventoryService) { this.inventoryService = inventoryService; }

    public void processRequest(Reservation r) {
        String type = r.getRoomType();
        if (!inventoryService.isAvailable(type)) {
            System.out.println("No rooms available for " + type + " for " + r.getGuestName());
            return;
        }
        String roomId;
        do { roomId = type.substring(0, 2).toUpperCase() + new Random().nextInt(1000); }
        while (allocatedRoomIds.contains(roomId));
        allocatedRoomIds.add(roomId);
        roomAllocationMap.putIfAbsent(type, new HashSet<>());
        roomAllocationMap.get(type).add(roomId);
        r.setRoomId(roomId);
        inventoryService.decrement(type);
        bookingHistory.add(r);
        System.out.println("Booking CONFIRMED:");
        r.displayReservation();
    }

    public void displayAllocations() { System.out.println(roomAllocationMap); }
    public List<Reservation> getBookingHistory() { return bookingHistory; }
}

public class HotelBookingApp {
    private static final String DATA_FILE = "hotel_data.ser";

    public static void main(String[] args) {
        BookingRequestQueue queue;
        InventoryService inventory;
        BookingService bookingService;

        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                queue = (BookingRequestQueue) ois.readObject();
                inventory = (InventoryService) ois.readObject();
                bookingService = (BookingService) ois.readObject();
                System.out.println("Data loaded from file.");
            } catch (Exception e) {
                queue = new BookingRequestQueue();
                inventory = new InventoryService();
                bookingService = new BookingService(inventory);
                System.out.println("Failed to load data, starting fresh.");
            }
        } else {
            queue = new BookingRequestQueue();
            inventory = new InventoryService();
            bookingService = new BookingService(inventory);
        }

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Single Room"));

        while (queue.hasRequests()) {
            Reservation r = queue.getNextRequest();
            bookingService.processRequest(r);
        }

        bookingService.displayAllocations();
        inventory.displayInventory();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(queue);
            oos.writeObject(inventory);
            oos.writeObject(bookingService);
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save data.");
        }
    }
}