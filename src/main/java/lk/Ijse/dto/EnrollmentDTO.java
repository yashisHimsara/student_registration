package lk.Ijse.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class EnrollmentDTO {
    private String eid;
    private String sid;
    private String Studentname;
    private String cid;
    private String Coursename;
    private LocalDate date;
    private Double upfrontpayment;
    private Double remainingfee;
    private String comment;
}
