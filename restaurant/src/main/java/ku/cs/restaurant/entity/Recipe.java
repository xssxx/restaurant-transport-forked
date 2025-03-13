package ku.cs.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Recipe {
    @EmbeddedId
    private RecipeKey id;

    @Column(name = "qty", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double qty;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "i_id")
    private Ingredient ingredient;

    @ManyToOne
    @MapsId("foodId")
    @JoinColumn(name = "f_id")
    private Food food;
}
