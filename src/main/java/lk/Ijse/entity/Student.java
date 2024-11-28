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
public class Student {
    @Id
    private String sid;
    private String name;
    private String address;
    private String tel;
    private String email;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    private List<Enrollment> enrollmentList = new ArrayList<>();

    public void addEnrollment(Enrollment enrollment) {
        enrollmentList.add(enrollment);
        enrollment.setStudent(this);
    }

    public void removeEnrollment(Enrollment enrollment) {
        enrollmentList.remove(enrollment);
        enrollment.setStudent(null);
    }

    public Student(String sid, String name, String address, String tel, String email) {
        this.sid = sid;
        this.name = name;
        this.address = address;
        this.tel = tel;
        this.email = email;
    }
}
