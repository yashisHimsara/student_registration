package lk.Ijse.entity.tm;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter

public class UserTm {
    private String userid;
    private String username;
    private String password;
    private String email;
    private String role;
}
