package tr.edu.mu.se3006.business;
import tr.edu.mu.se3006.persistence.ProductRepository;

public class OrderService {
    // TODO: Define ProductRepository dependency
    
    // TODO: Implement Constructor Injection
    
    public void placeOrder(Long productId, int quantity) {
        // TODO 1: Find product via repository
        // TODO 2: Check stock (throw IllegalArgumentException if insufficient)
        // TODO 3: Reduce stock
        // TODO 4: Save updated product
    }
}
