import java.util.*;
import java.util.concurrent.*;

class Reservation {
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

class BookingRequestQueue {
    private Queue<Reservation> queue = new ConcurrentLinkedQueue<>();
    public void addRequest(Reservation r) { queue.add(r); }
    public Reservation getNextRequest() { return queue.poll(); }
    public boolean hasRequests() { return !queue.isEmpty(); }
}

class InventoryService {
    private Map<String, Integer> inventory = new ConcurrentHashMap<>();
    public InventoryService() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }
    public boolean isAvailable(String roomType) { return inventory.getOrDefault(roomType, 0) > 0; }
    public synchronized void decrement(String roomType) { inventory.put(roomType, inventory.get(roomType) - 1); }
    public void displayInventory() { System.out.println(inventory); }
}

class BookingService {
    private InventoryService inventoryService;
    private Set<String> allocatedRoomIds = Collections.synchronizedSet(new HashSet<>());
    private Map<String, Set<String>> roomAllocationMap = new ConcurrentHashMap<>();

    public BookingService(InventoryService inventoryService) { this.inventoryService = inventoryService; }

    public void processRequest(Reservation r) {
        String type = r.getRoomType();
        synchronized (this) {
            if (!inventoryService.isAvailable(type)) {
                System.out.println("No rooms available for " + type + " for " + r.getGuestName());
                return;
            }
            String roomId;
            do { roomId = type.substring(0, 2).toUpperCase() + new Random().nextInt(1000); }
            while (allocatedRoomIds.contains(roomId));
            allocatedRoomIds.add(roomId);
            roomAllocationMap.putIfAbsent(type, Collections.synchronizedSet(new HashSet<>()));
            roomAllocationMap.get(type).add(roomId);
            r.setRoomId(roomId);
            inventoryService.decrement(type);
            System.out.println("Booking CONFIRMED:");
            r.displayReservation();
        }
    }

    public void displayAllocations() { System.out.println(roomAllocationMap); }
}

public class HotelBookingApp {
    public static void main(String[] args) throws InterruptedException {
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));
        queue.addRequest(new Reservation("David", "Single Room"));
        queue.addRequest(new Reservation("Eve", "Double Room"));

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(inventory);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        while (queue.hasRequests()) {
            Reservation r = queue.getNextRequest();
            executor.submit(() -> bookingService.processRequest(r));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        bookingService.displayAllocations();
        inventory.displayInventory();
    }
}