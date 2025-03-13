package ku.cs.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import ku.cs.restaurant.common.Status;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Food {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "f_id")
    private UUID id;

    @Column(name = "f_image")
    private String imagePath;

    @Column(name = "f_name")
    private String name;

    @Column(name = "f_price", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "f_status")
    private Status status;

    @OneToMany(mappedBy = "food")
    @JsonIgnore
    private List<Recipe> recipes = new ArrayList<>();
}

