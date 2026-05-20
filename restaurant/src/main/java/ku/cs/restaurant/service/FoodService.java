package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.food.FoodCreateRequest;
import ku.cs.restaurant.dto.food.FoodsResponse;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FoodService {
    private final FoodRepository foodRepository;

    public FoodService(FoodRepository foodRepository) {
        this.foodRepository = foodRepository;
    }

    public Food createFood(Food food) {
        return foodRepository.save(food);
    }

    public List<Food> getFoodsByStatus(Status status) {
        return foodRepository.findFoodsByStatus(status);
    }

    public Optional<Food> getFoodById(UUID id) {
        return foodRepository.findFoodById(id);
    }

    public List<FoodsResponse> getAllFoods() {
        return foodRepository.findAllFoodsWithMax();
    }

    public Map<UUID, Food> getFoodsMapByIds(List<UUID> ids) {
        if (ids.isEmpty()) return Collections.emptyMap();
        return foodRepository.findAllByIdWithRecipes(ids)
                .stream()
                .collect(Collectors.toMap(Food::getId, Function.identity()));
    }

    public void deleteFoodById(UUID id) {
        foodRepository.deleteById(id);
    }

    public Food createFoodEntity(FoodCreateRequest request, String imagePath) {
        return request.toFood(imagePath);
    }
}
