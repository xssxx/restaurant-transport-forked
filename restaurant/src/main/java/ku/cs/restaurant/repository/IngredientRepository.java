package ku.cs.restaurant.repository;

import jakarta.transaction.Transactional;
import jakarta.persistence.LockModeType;
import ku.cs.restaurant.entity.Ingredient;
import ku.cs.restaurant.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
    List<Ingredient> findByStatus(Status status);
    Optional<Ingredient> findById(UUID id);

    // SELECT ... FOR UPDATE — locks rows so concurrent placeOrder reads see consistent stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Ingredient i WHERE i.id IN :ids")
    List<Ingredient> findAllByIdsForUpdate(@Param("ids") Set<UUID> ids);

    @Modifying
    @Transactional
    @Query("""
    UPDATE Ingredient i
    SET i.qty = i.qty - (
        SELECT COALESCE(SUM(r.qty * ol.qty), 0)
        FROM OrderLine ol
        JOIN ol.food f
        JOIN f.recipes r
        WHERE ol.order.id = :orderId
        AND r.ingredient.id = i.id
    )
    WHERE i.id IN (
        SELECT r.ingredient.id
        FROM OrderLine ol
        JOIN ol.food f
        JOIN f.recipes r
        WHERE ol.order.id = :orderId
    )
    """)
    void decreaseIngredientQtyByOrderId(@Param("orderId") UUID orderId);

    @Modifying
    @Transactional
    @Query("UPDATE Ingredient i SET i.status = :status WHERE i.qty <= 0")
    void markDepletedAsOutOfStock(@Param("status") Status status);
}
