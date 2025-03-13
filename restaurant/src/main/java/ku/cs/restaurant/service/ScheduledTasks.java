package ku.cs.restaurant.service;

import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.common.OrderStatus;
import ku.cs.restaurant.repository.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    private final OrderRepository orderRepository;

    public ScheduledTasks(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedRate = 360000)
    @Transactional
    // if order did not pay for 24 hours, it'll get cancelled
    public void updateOrderStatus() {
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);
        List<Order> overdueOrders = orderRepository.findPendingOrdersCreatedBefore(yesterday);

        for (Order order : overdueOrders) {
            order.setStatus(OrderStatus.CANCEL);
        }

        orderRepository.saveAll(overdueOrders);
    }
}
