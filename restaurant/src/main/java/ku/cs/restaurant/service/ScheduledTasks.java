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

    @Scheduled(fixedRate = 3600000) // every 1 hour
    @Transactional
    public void cancelOverdueOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<Order> overdueOrders = orderRepository.findPendingOrdersCreatedBefore(cutoff);

        for (Order order : overdueOrders) {
            order.transitionTo(OrderStatus.CANCEL);
        }
    }
}
