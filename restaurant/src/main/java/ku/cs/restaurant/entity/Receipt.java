package ku.cs.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class Receipt {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "b_id")
    private UUID id;

    @Column(name = "b_total",columnDefinition = "DECIMAL(10,2)")
    private double total;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
