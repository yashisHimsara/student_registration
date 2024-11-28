package lk.Ijse.dto;

import lombok.*;

import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class PaymentDTO {
    private String id;
    private String eid;
    private Double amount;
    private LocalDate date;
}
