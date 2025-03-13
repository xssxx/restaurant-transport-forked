package ku.cs.restaurant.service;

import ku.cs.restaurant.dto.financial.CreateFinancialRequest;
import ku.cs.restaurant.entity.Ingredient;
import ku.cs.restaurant.repository.IngredientRepository;
import ku.cs.restaurant.common.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IngredientService {
    private final IngredientRepository repository;
    private final FinancialService financialService;

    public IngredientService(IngredientRepository repository, FinancialService financialService) {
        this.repository = repository;
        this.financialService = financialService;
    }

    // Create a new ingredient
    public Ingredient createIngredient(Ingredient ingredient) {
        CreateFinancialRequest req = new CreateFinancialRequest();
        req.setExpense(ingredient.getPrice());
        req.setIncome(0);
        financialService.addFinancial(req);
        return repository.save(ingredient);
    }

    // Find ingredients by status (e.g., OUT_OF_STOCK, IN_STOCK)
    public List<Ingredient> findIngredientsByStatus(Status status) {
        return repository.findByStatus(status);
    }

    // Find an ingredient by ID
    public Optional<Ingredient> findIngredientById(UUID id) {
        return repository.findById(id);
    }

    // Find all ingredients
    public List<Ingredient> findIngredients() {
        return repository.findAll();
    }

    // update quantity of an ingredient
    public Optional<Ingredient> updateQty(UUID id, double amount) {
        Optional<Ingredient> optionalIngredient = repository.findById(id);
        optionalIngredient.ifPresent(ingredient -> {
            ingredient.setQty(amount);

            // Set status to OUT_OF_STOCK if quantity is 0 or less
            if (ingredient.getQty() <= 0.0) {
                ingredient.setStatus(Status.OUT_OF_STOCK);
                ingredient.setQty(0.0); // Ensure quantity doesn't go negative
            } else if (ingredient.getStatus() == Status.OUT_OF_STOCK && amount > 0.0) {
                ingredient.setStatus(Status.AVAILABLE);
            }

            CreateFinancialRequest req = new CreateFinancialRequest();
            req.setIncome(0.0);

            req.setExpense(ingredient.getPrice() * amount); // price * new amount
            financialService.addFinancial(req);

            repository.save(ingredient);
        });
        return optionalIngredient; // Return the updated ingredient or empty if not found
    }

    // Update ingredient status
    public Optional<Ingredient> updateStatus(UUID id, Status newStatus) {
        Optional<Ingredient> optionalIngredient = repository.findById(id);
        optionalIngredient.ifPresent(ingredient -> {
            ingredient.setStatus(newStatus);
            repository.save(ingredient);
        });
        return optionalIngredient;
    }

    // Delete an ingredient
    public void deleteIngredient(UUID id) {
        repository.deleteById(id);
    }

    public void decreaseIngredientQtyByOrderId(UUID id) {
        repository.decreaseIngredientQtyByOrderId(id);
    }
}
