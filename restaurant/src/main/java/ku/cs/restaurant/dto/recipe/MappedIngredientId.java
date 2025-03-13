package ku.cs.restaurant.dto.recipe;

import lombok.Data;

import java.util.UUID;

@Data
public class MappedIngredientId {
    private UUID id;
    private double quantity;
}
