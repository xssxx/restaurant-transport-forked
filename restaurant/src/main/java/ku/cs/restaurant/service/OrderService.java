package ku.cs.restaurant.service;

import ku.cs.restaurant.common.OrderStatus;
import ku.cs.restaurant.dto.Payment.PaymentResponse;
import ku.cs.restaurant.dto.financial.CreateFinancialRequest;
import ku.cs.restaurant.dto.order.FoodOrder;
import ku.cs.restaurant.dto.order.OrderRequest;
import ku.cs.restaurant.entity.*;
import ku.cs.restaurant.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final FoodService foodService;
    private final ReceiptService receiptService;
    private final OrderLineService orderLineService;
    private final PaymentService paymentService;
    private final IngredientService ingredientService;
    private final FinancialService financialService;

    public Optional<Order> findOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(double total, User user, Receipt receipt) {
        Order order = new Order();
        order.setTotal(total);
        order.setUser(user);
        order.setReceipt(receipt);
        return orderRepository.save(order);
    }

    public List<Order> findOrders() {
        return orderRepository.findAll();
    }

    public List<Order> findOrderByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public Optional<Order> updateOrderStatusById(UUID id, OrderStatus newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            order.transitionTo(newStatus);
            orderRepository.save(order);
        });
        return optionalOrder;
    }

    public void addPaymentLink(UUID id, String link) {
        findOrderById(id).ifPresent(order -> order.setPaymentLink(link));
    }

    @Transactional
    public PaymentResponse placeOrder(OrderRequest request, User user) throws Exception {
        List<FoodOrder> foodOrders = request.getFoods();

        // 1. Load food + recipe structure (no ingredient entities loaded — Recipe.ingredient is LAZY)
        List<UUID> foodIds = foodOrders.stream()
                .map(fo -> fo.getFood().getId())
                .collect(Collectors.toList());
        Map<UUID, Food> foodMap = foodService.getFoodsMapByIds(foodIds);

        for (FoodOrder fo : foodOrders) {
            Food food = foodMap.get(fo.getFood().getId());
            if (food == null)
                throw new NoSuchElementException("Food not found: " + fo.getFood().getId());
            if (food.getRecipes().isEmpty())
                throw new IllegalArgumentException("No recipes for food: " + food.getId());
        }

        // 2. Collect ingredient IDs from the composite key — no lazy-load triggered
        Set<UUID> ingredientIds = foodMap.values().stream()
                .flatMap(f -> f.getRecipes().stream())
                .map(r -> r.getId().getIngredientId())
                .collect(Collectors.toSet());

        // 3. Acquire SELECT FOR UPDATE on all required ingredient rows.
        //    Because Recipe.ingredient is LAZY and was never fetched, these entities
        //    are not in the Hibernate session cache — Hibernate issues a real DB lock query.
        //    Concurrent placeOrder calls on the same ingredients will block here until
        //    the first transaction commits, then read the freshly updated stock.
        Map<UUID, Ingredient> lockedIngredients = ingredientService.findAllByIdsForUpdate(ingredientIds)
                .stream()
                .collect(Collectors.toMap(Ingredient::getId, Function.identity()));

        // 4. Validate stock against the locked (authoritative) qtys
        for (FoodOrder fo : foodOrders) {
            Food food = foodMap.get(fo.getFood().getId());
            for (Recipe recipe : food.getRecipes()) {
                double required = fo.getQuantity() * recipe.getQty();
                Ingredient locked = lockedIngredients.get(recipe.getId().getIngredientId());
                if (locked == null || required > locked.getQty())
                    throw new IllegalStateException(
                            "Insufficient ingredient: " + recipe.getId().getIngredientId() +
                            " | Required: " + required + ", Available: " + (locked != null ? locked.getQty() : 0));
            }
        }

        Receipt receipt = receiptService.createReceipt(request.calculateTotal());
        Order order = createOrder(request.calculateTotal(), user, receipt);

        for (FoodOrder fo : foodOrders) {
            orderLineService.createOrderLine(fo.getQuantity(), order, foodMap.get(fo.getFood().getId()));
        }

        PaymentResponse response = paymentService.createPaymentLink(order);
        addPaymentLink(order.getId(), response.getPaymentLink());
        return response;
    }

    @Transactional
    public void cookOrder(UUID orderId) {
        // Atomic compare-and-swap: PENDING → COOKING.
        // Only one concurrent caller can get rows=1; the other gets 0 and returns early.
        // This prevents double-deduction without any additional row-level lock overhead.
        int updated = orderRepository.updateStatusIfExpected(orderId, OrderStatus.PENDING, OrderStatus.COOKING);
        if (updated == 0) return;

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        CreateFinancialRequest req = new CreateFinancialRequest();
        req.setIncome(order.getTotal());
        req.setExpense(0.0);
        financialService.addFinancial(req);

        ingredientService.decreaseIngredientQtyByOrderId(orderId);
        ingredientService.markDepletedAsOutOfStock();
    }
}
