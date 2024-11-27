package Ijse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class Payment {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "eid")
    private Enrollment enrollment;

    private Double amount;

    private LocalDate date;
}
