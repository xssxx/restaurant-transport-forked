package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.food.FoodCreateRequest;
import ku.cs.restaurant.dto.food.FoodDeleteDto;
import ku.cs.restaurant.dto.food.FoodsResponse;
import ku.cs.restaurant.dto.recipe.IngredientQtyRequest;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FoodController {

    private final FoodService service;
    private final RecipeService recipeService;

    @PostMapping("/foods")
    @Transactional
    public ResponseEntity<ApiResponse<Food>> createMenu(@RequestPart("food") FoodCreateRequest foodCreateRequest,
                                                        @RequestPart("ingredients") IngredientQtyRequest ingredients,
                                                        @RequestPart("image") MultipartFile image) {
        try {
            String fileName = image.getOriginalFilename();

            Food food = service.createFoodEntity(foodCreateRequest, fileName);
            Food createdFood = service.createFood(food);

            recipeService.createRecipes(ingredients, createdFood);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Food created successfully.", createdFood));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Data integrity violation: " + e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "An error occurred: " + e.getMessage(), null));
        }
    }

    @GetMapping("/foods/status/{status}")
    public ResponseEntity<ApiResponse<List<Food>>> getByStatus(@PathVariable String status) {
        try {
            Status productStatus = Status.valueOf(status.toUpperCase());
            List<Food> foods = service.getFoodsByStatus(productStatus);
            return ResponseEntity.ok(new ApiResponse<>(true, "Foods retrieved successfully.", foods));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Invalid status value.", null));
        }
    }

    @GetMapping("/foods")
    public ResponseEntity<ApiResponse<List<FoodsResponse>>> getAll() {
        List<FoodsResponse> foods = service.getAllFoods();
        String imageBaseUrl = "http://localhost:8000/images/";


        for (FoodsResponse food : foods) {
            String imagePath = food.getImagePath().replace("\\", "/");
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            food.setImagePath(imageBaseUrl + fileName);
        }

        return ResponseEntity.ok(new ApiResponse<>(true, "Foods retrieved successfully.", foods));
    }

    @DeleteMapping("/foods")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@RequestBody FoodDeleteDto deleteDto) {
        try {
            service.deleteFoodById(deleteDto.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ApiResponse<>(true, "Food deleted " +
                    "successfully.",
                    null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Food not found.", null));
        }
    }
}
