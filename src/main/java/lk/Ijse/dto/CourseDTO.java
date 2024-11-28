package lk.Ijse.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class CourseDTO {
    private String cid;
    private String coursename;
    private String duration;
    private Double fee;
}
