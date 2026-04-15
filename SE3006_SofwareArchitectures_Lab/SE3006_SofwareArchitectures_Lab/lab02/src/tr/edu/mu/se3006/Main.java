package tr.edu.mu.se3006;
import tr.edu.mu.se3006.catalog.CatalogService;
import tr.edu.mu.se3006.catalog.CatalogFactory;
import tr.edu.mu.se3006.orders.OrderController;
import tr.edu.mu.se3006.orders.OrdersFactory;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 System Starting in Modular Monolith Mode...");
        System.out.println("----------------------------------------------\n");
        
        // TODO 1: Create the Catalog Module via its Factory
        CatalogService catalog = CatalogFactory.create();
        
        // TODO 2: Create the Orders Module via its Factory, passing the catalog module
        OrderController controller = OrdersFactory.create(catalog);
        
        System.out.println("--- Test Scenarios ---\n");
        // TODO 3: Call handleUserRequest via the controller to test the system
        // Test 1: Successful order - MacBook Pro (sufficient stock)
        controller.handleUserRequest(1L, 2);
        
        System.out.println();
        
        // Test 2: Successful order - Logitech Mouse
        controller.handleUserRequest(2L, 5);
        
        System.out.println();
        
        // Test 3: Failed order - Trying to buy more than available stock
        controller.handleUserRequest(1L, 10);
        
        System.out.println();
        
        // Test 4: Failed order - Invalid product (Product doesn't exist)
        controller.handleUserRequest(999L, 1);
    }
}
