package tr.edu.mu.se3006;
import tr.edu.mu.se3006.presentation.OrderController;
import tr.edu.mu.se3006.business.OrderService;
import tr.edu.mu.se3006.persistence.ProductRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 System Starting...\n");
        
        // TODO 1: Create the lowest layer (ProductRepository)
        ProductRepository productRepository = new ProductRepository();
        
        // TODO 2: Create the middle layer (OrderService) and inject the repository
        OrderService orderService = new OrderService(productRepository);
        
        // TODO 3: Create the top layer (OrderController) and inject the service
        OrderController orderController = new OrderController(orderService);
        
        System.out.println("--- Test Scenarios ---");

        // TODO 4: Call handleUserRequest via the controller to test the system

        orderController.handleUserRequest(1L,2);
        orderController.handleUserRequest(2L,2);
        orderController.handleUserRequest(2L,20);
        // e.g., controller.handleUserRequest(1L, 2);
    }


}
