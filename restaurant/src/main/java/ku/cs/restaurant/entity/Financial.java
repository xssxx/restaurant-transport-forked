package ku.cs.restaurant.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;

@Data
@Entity
public class Financial {
    @Id
    @JdbcTypeCode(SqlTypes.LOCAL_DATE)
    private LocalDate date;

    @Column(name = "income", columnDefinition = "DECIMAL(10,2)")
    private double income;
    @Column(name = "expense", columnDefinition = "DECIMAL(10,2)")
    private double expense;
    @Column(name = "total", columnDefinition = "DECIMAL(10,2)")
    private double total;
}
