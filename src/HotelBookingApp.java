import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean active = true;

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

    public boolean isActive() {
        return active;
    }

    public void cancel() {
        this.active = false;
    }

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Room Type: " + roomType + ", Room ID: " + roomId + ", Status: " + (active ? "ACTIVE" : "CANCELLED"));
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

    public void increment(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public void displayInventory() {
        System.out.println(inventory);
    }
}

class BookingService {
    private InventoryService inventoryService;
    private Set<String> allocatedRoomIds = new HashSet<>();

    public BookingService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public boolean processRequest(Reservation r) {
        String type = r.getRoomType();

        if (!inventoryService.isAvailable(type)) {
            System.out.println("No rooms available for " + type + " for " + r.getGuestName());
            return false;
        }

        String roomId;
        do {
            roomId = type.substring(0, 2).toUpperCase() + new Random().nextInt(1000);
        } while (allocatedRoomIds.contains(roomId));

        allocatedRoomIds.add(roomId);

        r.setRoomId(roomId);

        inventoryService.decrement(type);

        System.out.println("Booking CONFIRMED:");
        r.displayReservation();

        return true;
    }

    public void releaseRoom(String roomId) {
        allocatedRoomIds.remove(roomId);
    }
}

class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public Reservation findByRoomId(String roomId) {
        for (Reservation r : history) {
            if (r.getRoomId().equals(roomId)) {
                return r;
            }
        }
        return null;
    }
}

class CancellationService {
    private InventoryService inventoryService;
    private BookingService bookingService;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(InventoryService inventoryService, BookingService bookingService) {
        this.inventoryService = inventoryService;
        this.bookingService = bookingService;
    }

    public void cancelReservation(String roomId, BookingHistory history) {
        Reservation r = history.findByRoomId(roomId);

        if (r == null) {
            System.out.println("Cancellation FAILED: Reservation not found");
            return;
        }

        if (!r.isActive()) {
            System.out.println("Cancellation FAILED: Already cancelled");
            return;
        }

        rollbackStack.push(roomId);

        inventoryService.increment(r.getRoomType());

        bookingService.releaseRoom(roomId);

        r.cancel();

        System.out.println("Cancellation SUCCESS for Room ID: " + roomId);
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Double Room"));
        queue.addRequest(new Reservation("Charlie", "Suite Room"));

        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService(inventory);
        BookingHistory history = new BookingHistory();
        CancellationService cancellationService = new CancellationService(inventory, bookingService);

        while (queue.hasRequests()) {
            Reservation r = queue.getNextRequest();
            boolean success = bookingService.processRequest(r);
            if (success) {
                history.add(r);
            }
        }

        List<Reservation> list = history.getAll();

        if (!list.isEmpty()) {
            cancellationService.cancelReservation(list.get(0).getRoomId(), history);
        }

        for (Reservation r : history.getAll()) {
            r.displayReservation();
        }

        inventory.displayInventory();
    }
}