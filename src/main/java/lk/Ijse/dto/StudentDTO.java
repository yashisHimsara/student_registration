package lk.Ijse.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class StudentDTO {
    private String sid;
    private String name;
    private String address;
    private String tel;
    private String email;
}
