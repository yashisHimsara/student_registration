package lk.Ijse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class Enrollment {
    @Id
    private String eid;

    @ManyToOne
    @JoinColumn(name = "sid")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "cid")
    private Course course;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Payment> paymentList = new ArrayList<>();

    private LocalDate date;
    private Double upfrontpayment;
    private Double remainingfee;
    private String comment;

    public Enrollment(String eid, Student student, Course course, LocalDate date, Double upfrontpayment, Double remainingfee, String comment) {
        this.eid = eid;
        this.student = student;
        this.course = course;
        this.date = date;
        this.upfrontpayment = upfrontpayment;
        this.remainingfee = remainingfee;
        this.comment = comment;
    }
}
