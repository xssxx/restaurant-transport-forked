package ku.cs.restaurant.dto.food;

import ku.cs.restaurant.common.Status;
import lombok.Data;

import java.util.Date;

@Data
public class FoodCreateRequest {
    private String name;
    private double price;
    private Status status;
    private Date expireDate;
}
