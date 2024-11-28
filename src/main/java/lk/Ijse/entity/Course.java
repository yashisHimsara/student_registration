package lk.Ijse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Entity
public class Course {
    @Id
    private String cid;
    private String coursename;
    private String duration;
    private Double fee;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Enrollment> enrollmentList = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        enrollmentList.add(enrollment);
        enrollment.setCourse(this);  // This ensures the enrollment knows about the course
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollmentList.remove(enrollment);
        enrollment.setCourse(null); // Break the relationship
    }

    public Course(String cid, String coursename, String duration, Double fee) {
        this.cid = cid;
        this.coursename = coursename;
        this.duration = duration;
        this.fee = fee;
    }
}
