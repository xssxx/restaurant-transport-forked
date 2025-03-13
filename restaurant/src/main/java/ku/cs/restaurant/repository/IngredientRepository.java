package ku.cs.restaurant.repository;

import jakarta.transaction.Transactional;
import ku.cs.restaurant.entity.Ingredient;
import ku.cs.restaurant.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    List<Ingredient> findByStatus(Status status);
    Optional<Ingredient> findById(UUID id);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Ingredient i
    SET i.qty = i.qty - (
        SELECT SUM(r.qty * ol.qty)
        FROM Order o
        JOIN o.orderLines ol
        JOIN ol.food f
        JOIN f.recipes r
        WHERE o.id = :orderId
        GROUP BY r.ingredient.id
    )
    WHERE i.id IN (
        SELECT r.ingredient.id
        FROM Order o
        JOIN o.orderLines ol
        JOIN ol.food f
        JOIN f.recipes r
        WHERE o.id = :orderId
    )
    """)
    void decreaseIngredientQtyByOrderId(UUID orderId);
}
