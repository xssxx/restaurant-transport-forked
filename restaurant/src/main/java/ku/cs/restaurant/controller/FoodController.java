package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.food.FoodCreateRequest;
import ku.cs.restaurant.dto.food.FoodDeleteDto;
import ku.cs.restaurant.dto.food.FoodsResponse;
import ku.cs.restaurant.dto.recipe.IngredientQtyRequest;
import ku.cs.restaurant.entity.Food;
import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.service.FoodService;
import ku.cs.restaurant.service.ImageService;
import ku.cs.restaurant.service.IngredientService;
import ku.cs.restaurant.service.RecipeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class FoodController {
    private final FoodService service;
    private final RecipeService recipeService;
    private final FoodService foodService;
    private final ImageService imageService;

    public FoodController(FoodService service, RecipeService recipeService, IngredientService ingredientService, FoodService foodService, ImageService imageService) {
        this.service = service;
        this.recipeService = recipeService;
        this.foodService = foodService;
        this.imageService = imageService;
    }

    @PostMapping("/foods")
    @Transactional
    public ResponseEntity<ApiResponse<Food>> createMenu(@RequestPart("food") FoodCreateRequest foodCreateRequest,
                                                        @RequestPart("ingredients") IngredientQtyRequest ingredients,
                                                        @RequestPart("image") MultipartFile image) {
        try {
            String imagePath = imageService.saveImage("src/main/resources/images/foods", image);
            Food food = foodService.createFoodEntity(foodCreateRequest, imagePath);
            Food createdFood = service.createFood(food);
            recipeService.createRecipes(ingredients, createdFood);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Food created successfully.", createdFood));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to save image: " + e.getMessage(), null));
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
        String imageBaseUrl = "http://localhost:8088/images/foods/";
        foods.forEach(food -> food.setImagePath(imageBaseUrl + Paths.get(food.getImagePath()).getFileName()));
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
