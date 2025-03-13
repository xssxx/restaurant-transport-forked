package ku.cs.restaurant.dto.ingredient;

import jakarta.validation.constraints.NotBlank;
import ku.cs.restaurant.common.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateStatusRequest {
    @NotBlank
    private UUID id;
    @NotBlank
    private Status status;
}
