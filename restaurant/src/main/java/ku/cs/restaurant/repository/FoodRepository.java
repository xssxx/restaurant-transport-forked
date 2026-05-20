package ku.cs.restaurant.repository;

import ku.cs.restaurant.dto.food.FoodsResponse;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.common.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodRepository extends JpaRepository<Food, UUID> {
    List<Food> findFoodsByStatus(Status status);
    Optional<Food> findFoodById(UUID id);

    @Query("SELECT new ku.cs.restaurant.dto.food.FoodsResponse(f.id, f.name, f.imagePath, f.price, MIN(i.qty / r.qty)) " +
           "FROM Food f LEFT JOIN f.recipes r LEFT JOIN r.ingredient i " +
           "GROUP BY f.id, f.name, f.imagePath, f.price")
    List<FoodsResponse> findAllFoodsWithMax();

    @Query("SELECT DISTINCT f FROM Food f LEFT JOIN FETCH f.recipes WHERE f.id IN :ids")
    List<Food> findAllByIdWithRecipes(@Param("ids") List<UUID> ids);
}
