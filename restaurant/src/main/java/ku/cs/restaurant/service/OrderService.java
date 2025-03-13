package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.entity.Receipt;
import ku.cs.restaurant.entity.User;
import ku.cs.restaurant.repository.OrderRepository;
import ku.cs.restaurant.common.OrderStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Optional<Order> findOrderById(UUID id) {
        return orderRepository.findById(id);
    }

    // สร้างออเดอร์ใหม่ 
    public Order createOrder(double total, User user, Receipt receipt) {
        Order order = new Order();
        order.setTotal(total);
        order.setUser(user);
        order.setReceipt(receipt);
        return orderRepository.save(order);
    }

    // ดูทั้งหมด
    public List<Order> findOrders() {
        return orderRepository.findAll();
    }

    // ดูตามสถานะ
    public List<Order> findOrderByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    // อัพเดทสถานะ 
    public Optional<Order> updateOrderStatusById(UUID id, OrderStatus status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(orders -> {
            orders.setStatus(status);
            orderRepository.save(orders);
        });

        return optionalOrder;
    }

    public void addPaymentLink(UUID id, String link) {
        this.findOrderById(id).ifPresent(order -> {
            order.setPaymentLink(link);
        });
    }
}
