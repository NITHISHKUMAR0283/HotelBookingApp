import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room Type: " + roomType + ", Room ID: " + roomId);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }
}

class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public boolean isAvailable(String roomType) {
        return inventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        inventory.put(roomType, inventory.get(roomType) - 1);
    }

    public void displayInventory() {
        System.out.println(inventory);
    }
}

class BookingService {
    private InventoryService inventoryService;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private Map<String, Set<String>> roomAllocationMap = new HashMap<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void processRequest(Reservation r) {
        String type = r.getRoomType();

        if (!inventoryService.isAvailable(type)) {
            System.out.println("No rooms available for " + type + " for " + r.getGuestName());
            return;
        }

        String roomId;
        do {
            roomId = type.substring(0, 2).toUpperCase() + new Random().nextInt(1000);
        } while (allocatedRoomIds.contains(roomId));

        allocatedRoomIds.add(roomId);

        roomAllocationMap.putIfAbsent(type, new HashSet<>());
        roomAllocationMap.get(type).add(roomId);

        r.setRoomId(roomId);

        inventoryService.decrement(type);

        System.out.println("Booking CONFIRMED:");
        r.displayReservation();
    }

    public void displayAllocations() {
        System.out.println(roomAllocationMap);
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Suite Room"));

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(inventory);

        while (queue.hasRequests()) {
            Reservation r = queue.getNextRequest();
            bookingService.processRequest(r);
        }

        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}