package Ijse.Utill;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordVerifier {
    public static boolean verifyPassword(String password,String hashedPassword) {
        return BCrypt.checkpw(password,hashedPassword);
    }
}
