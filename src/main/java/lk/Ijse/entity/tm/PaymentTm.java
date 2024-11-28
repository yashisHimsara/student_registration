package lk.Ijse.entity.tm;
import lombok.*;
import java.time.LocalDate;
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class PaymentTm {
    private String id;
    private String eid;
    private Double amount;
    private LocalDate date;

    public PaymentTm(String id, Double amount, String eid, LocalDate date) {
        this.id = id;
        this.amount = amount;
        this.eid = eid;
        this.date = date;
    }
}
