package ku.cs.restaurant.repository;

import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {
    List<Food> findFoodsByStatus(Status status);
    Optional<Food> findFoodById(UUID id);
}
