package ku.cs.restaurant.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateQtyRequest {
    @NotBlank
    private UUID id;
    @NotBlank
    private double qty;
}
