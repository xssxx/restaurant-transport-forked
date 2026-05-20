package ku.cs.restaurant.dto.food;

import ku.cs.restaurant.common.Status;
import ku.cs.restaurant.entity.Food;
import lombok.Data;

import java.util.Date;

@Data
public class FoodCreateRequest {
    private String name;
    private double price;
    private Status status;
    private Date expireDate;

    public Food toFood(String imagePath) {
        Food food = new Food();
        food.setName(name);
        food.setPrice(price);
        food.setStatus(status);
        food.setImagePath(imagePath);
        return food;
    }
}
