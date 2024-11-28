package lk.Ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lk.Ijse.Utill.PasswordEncrypt;
import lk.Ijse.Utill.PasswordVerifier;
import lk.Ijse.Utill.Regex;
import lk.Ijse.Utill.TextField;
import lk.Ijse.bo.BoFactory;
import lk.Ijse.bo.custom.UserBo;
import lk.Ijse.bo.custom.impl.UserBoImpl;
import lk.Ijse.dto.UserDTO;
import lk.Ijse.entity.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private JFXButton btnadduser;

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnsearchuser;

    @FXML
    private JFXButton btnupdate;

    @FXML
    private TableColumn<?, ?> colemail;

    @FXML
    private TableColumn<?, ?> colpassword;

    @FXML
    private TableColumn<?, ?> colrole;

    @FXML
    private TableColumn<?, ?> coluserid;

    @FXML
    private TableColumn<?, ?> colusername;

    @FXML
    private TableView<User> tbluser;

    @FXML
    private JFXTextField txtemail;

    @FXML
    private JFXTextField txtpassword;

    @FXML
    private JFXPasswordField txtpassword1;

    @FXML
    private JFXTextField txtrepassword;

    @FXML
    private JFXPasswordField txtrepassword1;

    @FXML
    private JFXComboBox<String> txtrole;

    @FXML
    private JFXTextField txtsearch;

    @FXML
    private JFXTextField txtuserid;

    @FXML
    private JFXTextField txtusername;

    @FXML
    private AnchorPane userform;

    ObservableList<User> observableList;
    String ID;
    UserBo userBO = (UserBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.User);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setCellValueFactory();
        generateNextUserId();

        txtpassword.setVisible(false);
        txtrepassword.setVisible(false);
        txtrole.getItems().addAll("admin", "coordinator");
    }

    private void generateNextUserId() {

        String nextId = null;
        try {
            nextId = userBO.generateNewUserID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        txtuserid.setText(nextId);

    }

    private void setCellValueFactory() {
        coluserid.setCellValueFactory(new PropertyValueFactory<>("userid"));
        colusername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colpassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colrole.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void getAll() throws SQLException, ClassNotFoundException {
        observableList = FXCollections.observableArrayList();
        List<UserDTO> allUser = userBO.getAllUser();

        for (UserDTO userDTO : allUser){
            observableList.add(new User(userDTO.getUserid(),userDTO.getUsername(),userDTO.getPassword(),userDTO.getEmail(),userDTO.getRole()));
        }
        tbluser.setItems(observableList);
    }

    @FXML
    void btnadduserOnAction(ActionEvent event) throws Exception {
        String id = txtuserid.getText();
        String name = txtusername.getText();
        String password = txtpassword1.getText();
        String repassword = txtrepassword1.getText();
        String email = txtemail.getText();
        String role = (String) txtrole.getValue();

        int validationCode;
        if (id.isEmpty() || name.isEmpty() || password.isEmpty() || repassword.isEmpty() || email.isEmpty() || role == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid username!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid email format!").show();
            case 3 -> new Alert(Alert.AlertType.ERROR, "Invalid password format!").show();
            case 4 -> new Alert(Alert.AlertType.ERROR, "Invalid Repassword format!").show();
            default -> {
                if (userBO.UserIdExists(id)) {
                    new Alert(Alert.AlertType.ERROR, "User ID " + id + " already exists!").show();
                    return;
                }
                String encryptedrePassword = PasswordEncrypt.hashPassword(repassword);

                if (PasswordVerifier.verifyPassword(password, encryptedrePassword)) {
                    if (userBO.saveUser(new UserDTO(id, name, encryptedrePassword, email, role))) {
                        clearTextFileds();
                        generateNextUserId();
                        getAll();
                        new Alert(Alert.AlertType.CONFIRMATION, "Saved!!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Error!!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "Don't match Passwords!!").show();
                }
            }
        }

    }

    private void clearTextFileds() {
        txtuserid.clear();
        txtusername.clear();
        txtpassword.clear();
        txtpassword1.clear();
        txtrepassword.clear();
        txtrepassword1.clear();
        txtemail.clear();
        txtrole.getSelectionModel().clearSelection();
        generateNextUserId();
        txtpassword1.setEditable(true);
        txtpassword.setEditable(true);
        txtrepassword1.setEditable(true);
        txtrepassword.setEditable(true);
    }

    @FXML
    void btnclearOnAction(ActionEvent event) {
           clearTextFileds();
    }

    @FXML
    void btndeleteOnAction(ActionEvent event) throws Exception {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            if (!userBO.deleteUser(ID)) {
                new Alert(Alert.AlertType.ERROR, "An error occurred while deleting the user. Please try again.").show();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "User has been successfully deleted.").show();
            }
        }
        generateNextUserId();
        clearTextFileds();
        getAll();
    }

    @FXML
    void btnsearchuserOnAction(ActionEvent event) {
        String searchText = txtsearch.getText().toLowerCase();
        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User user : observableList) {
            if (user.getUserid().toLowerCase().contains(searchText)) {
                filteredList.add(user);
            }
        }
        tbluser.setItems(filteredList);
    }

    @FXML
    void btnupdateOnAction(ActionEvent event) throws Exception {
        String userid = txtuserid.getText().trim();
        String name = txtusername.getText().trim();
        String email = txtemail.getText().trim();
        String role = txtrole.getValue();
        User userById ;

        int validationCode;
        if (userid.isEmpty() || name.isEmpty() || email.isEmpty() || role == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid1();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid username!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid email format!").show();
            default -> {
                userById = userBO.findUserById(userid);
                if (userBO.updateUser(new UserDTO(userid, name, userById.getPassword(), email, role))) {
                    new Alert(Alert.AlertType.INFORMATION, "User updated successfully!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update user! Please try again.").show();
                }


                clearTextFileds();
                getAll();
            }
        }
    }


    @FXML
    void showPassword1OnMousePresseds(MouseEvent event) {
        txtpassword1.setVisible(false);
        txtpassword.setText(txtpassword1.getText());
        txtpassword.setVisible(true);
    }

    @FXML
    void showPassword1OnMouseReleased(MouseEvent event) {
        txtpassword1.setVisible(true);
        txtpassword.setVisible(false);
    }

    @FXML
    void showrePassword1OnMousePresseds(MouseEvent event) {
        txtrepassword1.setVisible(false);
        txtrepassword.setText(txtrepassword1.getText());
        txtrepassword.setVisible(true);
    }

    @FXML
    void showrePassword1OnMouseReleased(MouseEvent event) {
        txtrepassword1.setVisible(true);
        txtrepassword.setVisible(false);
    }


    public void rowOnMouseClicked(MouseEvent mouseEvent) {
        Integer index = tbluser.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        ID = String.valueOf(coluserid.getCellData(index));
        txtuserid.setText(ID);
        txtusername.setText(String.valueOf(colusername.getCellData(index)));
        txtemail.setText(String.valueOf(colemail.getCellData(index)));
        txtrole.setValue(String.valueOf(colrole.getCellData(index)));

        txtpassword1.setEditable(false);
        txtpassword.setEditable(false);
        txtrepassword1.setEditable(false);
        txtrepassword.setEditable(false);
    }

    @FXML
    void txtemailOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.EMAIL,txtemail);
    }

    @FXML
    void txtpassword1OnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PASSWORD,txtpassword1);
    }

    @FXML
    void txtrepassword1OnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PASSWORD,txtrepassword1);
    }

    @FXML
    void txtsearchOnKeyReleased(KeyEvent event) {

    }

    @FXML
    void txtuseridOnKeyReleased(KeyEvent event) {

    }

    @FXML
    void txtusernameOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.USERNAME,txtusername);
    }
    public int isValid() {
        if (!Regex.setTextColor(TextField.USERNAME, txtusername)) return 1;
        if (!Regex.setTextColor(TextField.EMAIL, txtemail)) return 2;
        if (!Regex.setTextColor(TextField.PASSWORD, txtpassword1)) return 3;
        if (!Regex.setTextColor(TextField.PASSWORD, txtrepassword1)) return 4;
        return 0;
    }
    public int isValid1() {
        if (!Regex.setTextColor(TextField.USERNAME, txtusername)) return 1;
        if (!Regex.setTextColor(TextField.EMAIL, txtemail)) return 2;
        return 0;
    }
}
