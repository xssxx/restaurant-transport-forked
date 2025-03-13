package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.food.FoodCreateRequest;
import ku.cs.restaurant.dto.food.FoodsResponse;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.entity.Ingredient;
import ku.cs.restaurant.entity.Recipe;
import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.repository.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FoodService {
    private final FoodRepository foodRepository;
    private final IngredientService ingredientService;

    public FoodService(FoodRepository foodRepository, IngredientService ingredientService) {
        this.foodRepository = foodRepository;
        this.ingredientService = ingredientService;
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

//    public List<Food> getAllFoods() {
//        return foodRepository.findAll();
//    }

    public List<FoodsResponse> getAllFoods() {
        List<Food> foods = foodRepository.findAll();
        List<FoodsResponse> foodDTOs = new ArrayList<>();

        for (Food food : foods) {
            // ดึงสูตรอาหารที่เกี่ยวข้องกับ food
            List<Recipe> recipes = food.getRecipes();
            double max = Double.MAX_VALUE;

            // คำนวณ max จากแต่ละสูตรอาหาร
            for (Recipe recipe : recipes) {
                Ingredient ingredient = ingredientService.findIngredientById(recipe.getIngredient().getId()).orElseThrow();
                double possibleMax = ingredient.getQty() / recipe.getQty();
                if (possibleMax < max) {
                    max = possibleMax; // เลือกค่าวัตถุดิบที่จำกัดมากที่สุด
                }
            }

            // สร้าง FoodDTO และใส่ค่า max
            FoodsResponse foodDTO = new FoodsResponse();
            foodDTO.setId(food.getId());
            foodDTO.setName(food.getName());
            foodDTO.setImagePath(food.getImagePath());
            foodDTO.setPrice(food.getPrice());
            foodDTO.setMax(max); // เพิ่ม max

            foodDTOs.add(foodDTO);
        }

        return foodDTOs;
    }

    public void deleteFoodById(UUID id) {
        foodRepository.deleteById(id);
    }

    public Food createFoodEntity(FoodCreateRequest request, String imagePath) {
        Food food = new Food();
        food.setName(request.getName());
        food.setPrice(request.getPrice());
        food.setStatus(request.getStatus());
        food.setImagePath(imagePath);
        return food;
    }
}
