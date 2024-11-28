package lk.Ijse.Utill;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import java.util.regex.Pattern;

public class Regex {

    private static String getRegex(TextField textField) {
        return switch (textField) {
            case USERNAME -> "^[a-zA-Z0-9_]{3,20}$";
            case NAME, ADDRESS -> "^[A-Za-z\\s]{3,}$";
            case TEL -> "^(?:\\+94\\s?)?(\\d{3})(?:-|\\s)?(\\d{7})$";
            case PRICE -> "^\\d+(\\.\\d{1,2})?$";
            case PASSWORD -> "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            case QTY -> "^\\d+$";
            case EMAIL -> "^([A-z])([A-z0-9.]){1,}[@]([A-z0-9]){1,10}[.]([A-z]){2,5}$";
            case DURATION -> "\\d+\\s+[a-zA-Z]+";
        };
    }

    public static String validate(TextField textField, String text) {
        if (text == null || text.trim().isEmpty()) {
            return "Field cannot be empty.";
        }

        String regex = getRegex(textField);
        if (!Pattern.matches(regex, text)) {
            return "Invalid format.";
        }

        return null; // Null indicates valid input
    }

    public static boolean setTextColor(TextField textField, JFXTextField textFieldInput) {
        String errorMessage = validate(textField, textFieldInput.getText());

        if (errorMessage == null) { // Valid input
            textFieldInput.setStyle("-fx-text-fill: white;"); // Change color to white
            return true;
        } else { // Invalid input
            textFieldInput.setStyle("-fx-text-fill: red;"); // Change color to red
            return false;
        }
    }

    public static boolean setTextColor(TextField textField, JFXPasswordField passwordField) {
        String errorMessage = validate(textField, passwordField.getText());

        if (errorMessage == null) { // Valid input
            passwordField.setStyle("-fx-text-fill: white;"); // Change color to white
            return true;
        } else { // Invalid input
            passwordField.setStyle("-fx-text-fill: red;"); // Change color to red
            return false;
        }
    }
}
