package ku.cs.restaurant.dto.food;

import lombok.Data;

import java.util.UUID;


@Data
public class FoodsResponse {
    private UUID id;
    private String imagePath;
    private String name;
    private double price;
    private String status;
    private double max;
}
