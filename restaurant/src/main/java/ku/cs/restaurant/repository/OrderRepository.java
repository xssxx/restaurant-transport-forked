package ku.cs.restaurant.repository;

import jakarta.transaction.Transactional;
import ku.cs.restaurant.entity.Order;
import ku.cs.restaurant.common.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findById(UUID id);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt <= :yesterday")
    List<Order> findPendingOrdersCreatedBefore(LocalDateTime yesterday);

    // Atomic compare-and-swap: transitions order from expectedStatus → newStatus.
    // Returns 1 if the row was updated (this call "won"), 0 if another transaction already changed it.
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id = :id AND o.status = :expected")
    int updateStatusIfExpected(@Param("id") UUID id,
                               @Param("expected") OrderStatus expected,
                               @Param("newStatus") OrderStatus newStatus);
}
