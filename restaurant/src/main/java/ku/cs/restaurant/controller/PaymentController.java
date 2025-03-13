package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.common.OrderStatus;
import ku.cs.restaurant.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
public class PaymentController {
    private final OrderService orderService;

    public PaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/payment/success/{id}")
    public ResponseEntity<ApiResponse<Order>> paymentSuccess(@PathVariable UUID id) {
        try {
            Optional<Order> updatedOrder = orderService.updateOrderStatusById(id, OrderStatus.COMPLETE);
            return updatedOrder.map(order -> ResponseEntity.ok(new ApiResponse<>(true, "Payment processed successfully.", order)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Order not found.", null)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }
}
