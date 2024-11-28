package lk.Ijse.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter

public class UserDTO{
    private String userid;
    private String username;
    private String password;
    private String email;
    private String role;
}
