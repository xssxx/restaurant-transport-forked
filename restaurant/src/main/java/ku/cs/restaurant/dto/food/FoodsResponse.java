package ku.cs.restaurant.dto.food;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@NoArgsConstructor
public class FoodsResponse {
    private UUID id;
    private String imagePath;
    private String name;
    private double price;
    private String status;
    private double max;

    public FoodsResponse(UUID id, String name, String imagePath, double price, Double computedMax) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
        this.max = computedMax != null ? computedMax : Double.MAX_VALUE;
    }
}
