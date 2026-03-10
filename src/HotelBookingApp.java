import java.util.LinkedList;
import java.util.Queue;

class Reservation {
    private String guestName;
    private String roomType;

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

    public void displayReservation() {
        System.out.println("Guest: " + guestName + ", Requested Room: " + roomType);
    }
}

class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasRequests() {
        return !requestQueue.isEmpty();
    }

    public void displayQueue() {
        System.out.println("===== Booking Request Queue =====");
        for (Reservation r : requestQueue) {
            r.displayReservation();
        }
        System.out.println("================================");
    }
}

public class UseCase5BookingRequestQueue {
    public static void main(String[] args) {
        BookingRequestQueue queue = new BookingRequestQueue();

        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");
        Reservation r3 = new Reservation("Charlie", "Suite Room");

        queue.addRequest(r1);
        queue.addRequest(r2);
        queue.addRequest(r3);

        queue.displayQueue();

        System.out.println("Processing requests in order of arrival:");
        while (queue.hasRequests()) {
            Reservation next = queue.getNextRequest();
            next.displayReservation();
        }

        System.out.println("All booking requests have been processed (inventory not modified).");
    }
}