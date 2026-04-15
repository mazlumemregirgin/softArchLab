package tr.edu.mu.se3006.presentation;
import tr.edu.mu.se3006.business.OrderService;

public class OrderController {
    // TODO: Define OrderService dependency and use Constructor Injection

    public void handleUserRequest(Long productId, int quantity) {
        System.out.println(">>> New Request: Product ID=" + productId + ", Quantity=" + quantity);
        // TODO: Call placeOrder inside a try-catch block. 
        // Print "✅ Order Confirmed" on success, or "❌ ERROR: [message]" on failure.
    }
}
