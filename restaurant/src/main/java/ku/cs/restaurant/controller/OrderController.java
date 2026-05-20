package ku.cs.restaurant.controller;

import ku.cs.restaurant.common.OrderStatus;
import ku.cs.restaurant.dto.Payment.PaymentResponse;
import ku.cs.restaurant.dto.food.FoodDto;
import ku.cs.restaurant.dto.food.FoodListDto;
import ku.cs.restaurant.dto.order.*;
import ku.cs.restaurant.entity.*;
import ku.cs.restaurant.service.OrderService;
import ku.cs.restaurant.service.UserService;
import ku.cs.restaurant.utils.JwtUtils;
import ku.cs.restaurant.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/order")
    public ResponseEntity<ApiResponse<PaymentResponse>> createOrder(
        @RequestBody OrderRequest orderRequest,
        @RequestHeader("Authorization") String jwt
    ) {
        try {
            String username = jwtUtils.getUserNameFromJwtToken(jwt);
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            if (orderRequest.getFoods().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse<>(false, "No food orders provided.", null));
            }

            PaymentResponse response = orderService.placeOrder(orderRequest, user);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Order created successfully.", response));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        try {
            List<Order> orders = orderService.findOrders();
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully.", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/user")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            User user = userService.getUserById(order.getUser().getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            return ResponseEntity.ok(new ApiResponse<>(true, "User fetched successfully.", UserResponse.from(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/receipt")
    public ResponseEntity<ApiResponse<Receipt>> getReceiptByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Receipt fetched successfully.", order.getReceipt()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/order")
    public ResponseEntity<ApiResponse<Order>> getOrderByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Order fetched successfully.", order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/{id}/food")
    public ResponseEntity<ApiResponse<FoodListDto>> getFoodByOrderId(@PathVariable("id") UUID id) {
        try {
            Order order = orderService.findOrderById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));

            List<FoodDto> foodDtos = order.getOrderLines().stream()
                    .map(ol -> {
                        FoodDto foodDto = new FoodDto();
                        foodDto.setFood(ol.getFood());
                        foodDto.setQty(ol.getQty());
                        return foodDto;
                    })
                    .collect(Collectors.toList());

            FoodListDto foodListDto = new FoodListDto();
            foodListDto.setFoods(foodDtos);

            return ResponseEntity.ok(new ApiResponse<>(true, "Foods fetched successfully.", foodListDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/order/status")
    public ResponseEntity<ApiResponse<List<Order>>> getOrdersByStatus(@RequestBody OrderStatus status) {
        try {
            List<Order> orders = orderService.findOrderByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully.", orders));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @PatchMapping("/order")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatusById(@RequestBody UpdateStatusRequest request) {
        try {
            Order updatedOrder = orderService.updateOrderStatusById(request.getId(), request.getStatus())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found"));
            return ResponseEntity.ok(new ApiResponse<>(true, "Order status updated successfully.", updatedOrder));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @PostMapping("/order/ingredient/{id}")
    public void updateOrderIngredientQty(@PathVariable UUID id) {
        orderService.cookOrder(id);
    }
}
