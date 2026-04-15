# SE 3006: Software Architecture - Laboratory 02: Modular Monolith Design

## Mazlum Emre Girgin - 220717005

## Abstract

This laboratory exercise demonstrates the transition from a Layered Architecture to a Modular Monolith architecture. We refactored a simple order management system to eliminate tight coupling and enforce module boundaries using Java package-private access modifiers, factory patterns, and interface-based communication. The report analyzes the architectural differences and explains how the modular approach addresses the limitations of traditional layered designs.

---

## 1. Introduction

In Laboratory 01, we implemented a basic Layered Architecture where business logic directly accessed data layer components. This straightforward approach quickly leads to architectural problems as the system grows:

- **Direct dependencies** between layers break encapsulation
- **Cross-cutting concerns** become scattered across layers
- **High coupling** makes the system difficult to test and maintain
- **Unclear domain boundaries** - modules become mixed together

This laboratory implements a Modular Monolith architecture, which organizes the system into vertical business domains while maintaining a single deployment unit.

---

## 2. Implementation

### 2.1 System Overview

The system consists of two independent modules:

1. **Catalog Module**: Manages product inventory and stock
2. **Orders Module**: Manages customer orders

Key implementation details:
- All internal classes use Java's `package-private` (default) access modifier
- Module communication occurs exclusively through public interfaces
- Factory classes handle internal object instantiation and wiring
- Constructor injection provides explicit dependency declaration

### 2.2 Task Implementation

#### Task 1: Catalog Module Internal Logic

Implemented the Catalog Module's data persistence and business logic:

```java
// Package-private repository
class ProductRepository {
    private Map<Long, Product> database = new HashMap<>();
    
    Product findById(Long id) {
        return database.get(id);
    }
    
    void save(Product product) {
        database.put(product.getId(), product);
    }
}

// Package-private service implementation
class CatalogServiceImpl implements CatalogService {
    private ProductRepository productRepository;
    
    CatalogServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public void checkAndReduceStock(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        if (product == null || product.getStock() < quantity) {
            throw new IllegalArgumentException(
                "Insufficient stock for product " + productId);
        }
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }
}
```

#### Task 2: Catalog Module Factory

The factory encapsulates the internal wiring and exposes only the public interface:

```java
public class CatalogFactory {
    public static CatalogService create() {
        ProductRepository productRepository = 
            new ProductRepository();
        CatalogServiceImpl catalogService = 
            new CatalogServiceImpl(productRepository);
        return catalogService;
    }
}
```

#### Task 3: Orders Module Logic

The Orders Module depends on the Catalog Module exclusively through its interface:

```java
class OrderService {
    private CatalogService catalogService;
    private OrderRepository orderRepository;
    
    OrderService(CatalogService catalogService, 
                 OrderRepository orderRepository) {
        this.catalogService = catalogService;
        this.orderRepository = orderRepository;
    }
    
    void placeOrder(Long productId, int quantity) {
        catalogService.checkAndReduceStock(productId, quantity);
        Order order = new Order(productId, quantity);
        orderRepository.save(order);
    }
}

public class OrderController {
    private OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    public void handleUserRequest(Long productId, int quantity) {
        System.out.println(">>> New Request: Product ID=" + 
            productId + ", Quantity=" + quantity);
        try {
            orderService.placeOrder(productId, quantity);
            System.out.println("✅ Order Confirmed");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }
}
```

#### Task 4: Orders Module Factory

Similar to the Catalog Module, the Orders Module factory wires internal components:

```java
public class OrdersFactory {
    public static OrderController create(
        CatalogService catalogService) {
        OrderRepository orderRepository = 
            new OrderRepository();
        OrderService orderService = 
            new OrderService(catalogService, orderRepository);
        OrderController controller = 
            new OrderController(orderService);
        return controller;
    }
}
```

---

## 3. Discussion

### 3.1 Layered Architecture vs. Modular Monolith

#### 3.1.1 Layered Architecture (Lab 01)

The traditional Layered Architecture organizes code by technical concerns:

```
Presentation Layer (Controllers)
        ↓
Business Logic Layer (Services)
        ↓
Persistence Layer (Repositories)
```

**Problems with Layered Architecture:**

1. **Cross-cutting Coupling**: Business logic directly instantiates and uses repository classes, creating compile-time dependencies between layers.

2. **Domain Boundaries Not Enforced**: All repositories are in the same layer. A service can access any repository, violating domain separation.

3. **Information Leakage**: Internal data structures (Product class, ProductRepository) are accessible to any layer, allowing unintended dependencies.

4. **Testing Complexity**: Classes cannot be tested in isolation because they tightly couple to concrete implementations rather than abstractions.

5. **Scalability Issues**: As the system grows, determining which services should interact becomes ambiguous. There is no clear contract between components.

**Example from Lab 01:**

```java
// OrderService directly depends on ProductRepository
class OrderService {
    private ProductRepository productRepository = 
        new ProductRepository();
    
    void placeOrder(Long productId, int quantity) {
        Product product = productRepository.findById(productId);
        // ... direct access to internal catalog structures
    }
}
```

Here, `OrderService` has direct knowledge of and access to the Catalog Module's internal persistent store. If ProductRepository changes, OrderService may break.

#### 3.1.2 Modular Monolith Architecture (Lab 02)

The Modular Monolith organizes code by business domains while maintaining a single deployment unit:

```
CATALOG MODULE              ORDERS MODULE
├─ ProductRepository        ├─ OrderService
├─ CatalogServiceImpl        ├─ OrderController
├─ CatalogService (Public)  ├─ OrderRepository
└─ CatalogFactory (Public)  └─ OrdersFactory (Public)

        (Communication via CatalogService interface)
```

**Benefits of Modular Monolith:**

1. **Enforced Module Boundaries**: Package-private classes are completely invisible to other modules. The Java compiler prevents unauthorized access. Orders Module cannot import ProductRepository.

2. **Interface-Based Communication**: Modules interact exclusively through public interfaces. OrderService calls `catalogService.checkAndReduceStock()`, not internal repository methods.

3. **Information Hiding**: Each module exposes a minimal API (Factory + Service Interface). All internal implementation details remain private.

4. **Reduced Coupling**: Orders Module has no compile-time or runtime dependency on ProductRepository or CatalogServiceImpl. Changes to Catalog's internal structure do not affect Orders.

5. **Testability**: Each module can be tested independently. Mock implementations of CatalogService can be easily injected into OrderService for unit testing.

6. **Clear Domain Logic**: Business domains are explicitly separated. Product management and Order management are distinct concerns with clear responsibilities.

#### 3.1.3 Code Comparison: Problem Resolution

**Problem 1: Orders Module needed to reduce product stock**

*Layered Approach (Lab 01):*
```java
// Orders directly manipulates catalog's data layer
OrderService orderService = new OrderService();
ProductRepository repo = new ProductRepository();
Product p = repo.findById(1L);
p.setStock(p.getStock() - 5);  // Direct manipulation!
repo.save(p);
```

This violates encapsulation and creates tight coupling.

*Modular Monolith Approach (Lab 02):*
```java
// Orders uses Catalog's public interface
CatalogService catalog = CatalogFactory.create();
OrderService orderService = 
    new OrderService(catalog, new OrderRepository());
orderService.placeOrder(1L, 5);  // Abstracted operation
```

The implementation detail of "reduce stock" is hidden inside CatalogServiceImpl.

**Problem 2: Modules accidentally sharing implementations**

*Layered Approach:*
Any class in the system can instantiate ProductRepository directly. There's nothing preventing misuse.

*Modular Monolith:*
```java
// This code WILL NOT COMPILE in Orders Module
class OrderService {
    void placeOrder(Long productId, int quantity) {
        // ERROR: ProductRepository is package-private
        ProductRepository repo = new ProductRepository();
    }
}
```

The compiler enforces module boundaries.

### 3.2 Architectural Principles Applied

- **Information Hiding**: Internal implementations are hidden using access modifiers
- **Single Responsibility Principle**: Each module manages one business domain
- **Dependency Inversion**: Modules depend on abstractions (interfaces), not concrete implementations
- **Open/Closed Principle**: Modules are closed for modification but open for extension through interfaces

---

## 4. Conclusion

The transition from Layered to Modular Monolith architecture demonstrates fundamental principles of software design:

1. **Vertical Organization Over Horizontal**: Organizing by business domain rather than technical layer provides better maintainability.

2. **Compile-Time Enforcement**: Using language features (package-private access) to enforce architectural rules is more reliable than documentation.

3. **Interface-Based Communication**: Modules should communicate through well-defined contracts, not through shared internal data structures.

4. **Loose Coupling, High Cohesion**: The modular approach reduces coupling between domains while keeping related functionality together.

While our implementation remains a monolith (single deployment unit), the modular structure provides a clear pathway for future evolution. If needed, individual modules could eventually be extracted into microservices while maintaining the same interface contracts.

---

**Date**: April 15, 2026  
**Course**: SE 3006: Software Architecture  
**Laboratory**: 02 - Modular Monolith Design
