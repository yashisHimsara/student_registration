package lk.Ijse.Controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.Ijse.Utill.PasswordVerifier;
import lk.Ijse.Utill.Regex;
import lk.Ijse.Utill.TextField;
import lk.Ijse.bo.BoFactory;
import lk.Ijse.bo.custom.UserBo;
import lk.Ijse.bo.custom.impl.UserBoImpl;
import lk.Ijse.dto.UserDTO;
import lk.Ijse.entity.User;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private AnchorPane loginform;

    @FXML
    private JFXTextField txtPassword1;

    @FXML
    private JFXPasswordField txtPassword2;

    @FXML
    private JFXTextField txtUsername;
    UserBo userBO = (UserBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.User);

    @FXML
    void btnLogInOnAction(ActionEvent event) throws Exception {
        String username  = txtUsername.getText().trim();
        String password = txtPassword2.getText().trim();
        User userByname = userBO.findUserByname(username);
        String userid;
        String role;
        int x ;

        int validationCode = isValid();
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid username!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid password format!").show();
            default -> {
                if (userByname != null) {
                    userid = userByname.getUserid();
                    role = userByname.getRole();
                    String password1 = userByname.getPassword();
                    if (PasswordVerifier.verifyPassword(password, password1)) {
                        if (role.equals("admin")) {
                            x = 1;
                            navigateToTheDashboard(x, userid);
                        } else {
                            x = 0;
                            navigateToTheDashboard(x, userid);
                        }

                    } else {
                        new Alert(Alert.AlertType.ERROR, "Invalid password! Please try again.").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Username does not exist! Please check your username.").show();
                }
            }
        }
    }

    @FXML
    void btnSignUpOnAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignUpForm.fxml"));
            AnchorPane coursesPane = loader.load();
            loginform.getChildren().setAll(coursesPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showPasswordOnMousePresseds(MouseEvent event) {
        txtPassword2.setVisible(false);
        txtPassword1.setText(txtPassword2.getText());
        txtPassword1.setVisible(true);
    }

    @FXML
    void showPasswordOnMouseReleased(MouseEvent event) {
        txtPassword2.setVisible(true);
        txtPassword1.setVisible(false);
    }

    private void navigateToTheDashboard(int role,String userid) throws IOException {
        loginform.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Dashboard.fxml"));
        Parent root = loader.load();
        DashboardController dashboardController = loader.getController();

        dashboardController.setRole(role);
        dashboardController.setUserid(userid);

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }
    @FXML
    void forgotpwonclick(MouseEvent event) throws Exception {
        String username  = txtUsername.getText().trim();
        if(!txtUsername.getText().isEmpty()){
        User userByname = userBO.findUserByname(username);
        if(userByname != null){
        String email = userByname.getEmail();

        if (true) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Reset");
            alert.setHeaderText(null);
            alert.setContentText("Password reset link has been sent to your email.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("The provided email is not registered.");
            alert.showAndWait();
        }
        }else {
            new Alert(Alert.AlertType.ERROR, "Username does not exist! Please check your username.").show();
        }
        }else {
            new Alert(Alert.AlertType.ERROR, "Username feild is empty!give username to reset password").show();

        }
    }

    @FXML
    void txtPassword2OnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PASSWORD,txtPassword2);
    }

    @FXML
    void txtUsernameOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.USERNAME,txtUsername);
    }
    public int isValid() {
        if (!Regex.setTextColor(TextField.USERNAME, txtUsername)) return 1;
        if (!Regex.setTextColor(TextField.PASSWORD, txtPassword2)) return 2;
        return 0;
    }

}
