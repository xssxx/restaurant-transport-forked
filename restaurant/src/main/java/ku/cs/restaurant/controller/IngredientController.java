package ku.cs.restaurant.controller;

import ku.cs.restaurant.dto.ApiResponse;
import ku.cs.restaurant.dto.financial.CreateFinancialRequest;
import ku.cs.restaurant.dto.ingredient.UpdateQtyRequest;
import ku.cs.restaurant.dto.ingredient.UpdateStatusRequest;
import ku.cs.restaurant.entity.Ingredient;
import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.service.FinancialService;
import ku.cs.restaurant.service.ImageService;
import ku.cs.restaurant.service.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class IngredientController {
    private final IngredientService service;
    private final ImageService imageService;
    private final FinancialService financialService;

    public IngredientController(IngredientService service, ImageService imageService, FinancialService financialService) {
        this.service = service;
        this.imageService = imageService;
        this.financialService = financialService;
    }

    // Create a new ingredient
    @PostMapping("/ingredient")
    public ResponseEntity<ApiResponse<Ingredient>> createIngredient(@RequestPart("ingredient") Ingredient ingredient, @RequestPart("image") MultipartFile image) {
        try {
            String imagePath = imageService.saveImage("src/main/resources/images/ingredients", image);
            ingredient.setImagePath(imagePath);

            Ingredient createdIngredient = service.createIngredient(ingredient);
            CreateFinancialRequest req = new CreateFinancialRequest();

            req.setExpense(createdIngredient.getPrice() * ingredient.getQty());
            req.setIncome(0);
            financialService.addFinancial(req);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Ingredient created successfully.", createdIngredient));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to create ingredient: " + e.getMessage(), null));
        }
    }

    // Get ingredients by status
    @GetMapping("/ingredient/status")
    public ResponseEntity<ApiResponse<List<Ingredient>>> findIngredientsByStatus(@RequestBody Status status) {
        try {
            List<Ingredient> ingredients = service.findIngredientsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Ingredients retrieved successfully.", ingredients));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to retrieve ingredients: " + e.getMessage(), null));
        }
    }

    // Get an ingredient by ID
    @GetMapping("/ingredient/{id}")
    public ResponseEntity<ApiResponse<Ingredient>> findIngredientById(@PathVariable UUID id) {
        return service.findIngredientById(id)
                .map(ingredient -> ResponseEntity.ok(new ApiResponse<>(true, "Ingredient retrieved successfully.", ingredient)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "Ingredient not found.", null)));
    }

    // Get all ingredients
    @GetMapping("/ingredient")
    public ResponseEntity<ApiResponse<List<Ingredient>>> findIngredients() {
        List<Ingredient> ingredients = service.findIngredients();
        String baseUrl = "http://localhost:8088/images/ingredients/";

        for (Ingredient ingredient : ingredients) {
            String imagePath = ingredient.getImagePath().replace("\\", "/");
            String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            ingredient.setImagePath(baseUrl + fileName);
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "All ingredients retrieved successfully.", ingredients));
    }

    // Update quantity of an ingredient
    @PatchMapping("/ingredient")
    public ResponseEntity<ApiResponse<Ingredient>> increaseQty(@RequestBody UpdateQtyRequest request) {
        try {
            Optional<Ingredient> updatedIngredient = service.updateQty(request.getId(), request.getQty());
            return updatedIngredient
                    .map(ingredient -> ResponseEntity.ok(new ApiResponse<>(true, "Ingredient quantity updated successfully.", ingredient)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Ingredient not found.", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to update quantity: " + e.getMessage(), null));
        }
    }

    // Update ingredient status
    @PatchMapping("/ingredient/status")
    public ResponseEntity<ApiResponse<Ingredient>> updateStatus(@RequestBody UpdateStatusRequest request) {
        try {
            Optional<Ingredient> updatedIngredient = service.updateStatus(request.getId(), request.getStatus());
            return updatedIngredient
                    .map(ingredient -> ResponseEntity.ok(new ApiResponse<>(true, "Ingredient status updated successfully.", ingredient)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ApiResponse<>(false, "Ingredient not found.", null)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Failed to update status: " + e.getMessage(), null));
        }
    }

    // Delete an ingredient
    @DeleteMapping("/ingredient")
    public ResponseEntity<ApiResponse<Void>> deleteIngredient(@RequestBody UUID id) {
        try {
            service.deleteIngredient(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponse<>(true, "Ingredient deleted successfully.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "Ingredient not found.", null));
        }
    }
}
