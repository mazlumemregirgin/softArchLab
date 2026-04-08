package tr.edu.mu.se3006.business;
import tr.edu.mu.se3006.domain.Order;
import tr.edu.mu.se3006.domain.Product;
import tr.edu.mu.se3006.persistence.ProductRepository;

public class OrderService {
    // TODO: Define ProductRepository dependency
    private ProductRepository productRepository;
    
    // TODO: Implement Constructor Injection

    public OrderService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public void placeOrder(Long productId, int quantity) {
        // TODO 1: Find product via repository
        Product product = productRepository.findById(productId);

        // TODO 2: Check stock (throw IllegalArgumentException if insufficient)
        if(product.getStock() < quantity){
            throw new IllegalArgumentException("Product Stock Not Enough");
        }

        // TODO 3: Reduce stock
        product.setStock(product.getStock() - quantity);

        // TODO 4: Save updated product
        productRepository.Save(product);

    }
}
