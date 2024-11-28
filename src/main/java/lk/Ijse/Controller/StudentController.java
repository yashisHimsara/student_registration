package lk.Ijse.Controller;

import com.jfoenix.controls.JFXButton;
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
import lk.Ijse.bo.custom.StudentBo;
import lk.Ijse.bo.custom.impl.StudentBoImpl;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.dto.UserDTO;
import lk.Ijse.entity.Student;
import lk.Ijse.entity.User;
import lk.Ijse.entity.tm.StudentTm;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class StudentController implements Initializable {

    @FXML
    private JFXButton btnaddstudent;

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnsearchstudent;

    @FXML
    private JFXButton btnupdate;

    @FXML
    private TableColumn<?, ?> coladdress;

    @FXML
    private TableColumn<?, ?> colemail;

    @FXML
    private TableColumn<?, ?> colstudentid;

    @FXML
    private TableColumn<?, ?> colstudentname;

    @FXML
    private TableColumn<?, ?> coltel;

    @FXML
    private AnchorPane studentform;

    @FXML
    private TableView<StudentTm> tblstudent;

    @FXML
    private JFXTextField txtaddress;

    @FXML
    private JFXTextField txtemail;

    @FXML
    private JFXTextField txtsearch;

    @FXML
    private JFXTextField txtstudentid;

    @FXML
    private JFXTextField txtstudentname;

    @FXML
    private JFXTextField txttel;
    ObservableList<StudentTm> observableList;
    String ID;
    StudentBo studentBo = (StudentBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Student);

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
    }

    private void generateNextUserId() {
        String nextId = null;
        try {
            nextId = studentBo.generateNewStudentID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        txtstudentid.setText(nextId);
    }

    private void setCellValueFactory() {
        colstudentid.setCellValueFactory(new PropertyValueFactory<>("sid"));
        colstudentname.setCellValueFactory(new PropertyValueFactory<>("name"));
        coladdress.setCellValueFactory(new PropertyValueFactory<>("address"));
        coltel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colemail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void getAll() throws SQLException, ClassNotFoundException {
        observableList = FXCollections.observableArrayList();
        List<StudentDTO> allStudent = studentBo.getAllStudent();

        for (StudentDTO studentDTO : allStudent){
            observableList.add(new StudentTm(studentDTO.getSid(),studentDTO.getName(),studentDTO.getAddress(),studentDTO.getTel(),studentDTO.getEmail()));
        }
        tblstudent.setItems(observableList);
    }

    @FXML
    void btnaddstudentOnAction(ActionEvent event) throws Exception {
        String id = txtstudentid.getText();
        String name = txtstudentname.getText();
        String address = txtaddress.getText();
        String tel = txttel.getText();
        String email = txtemail.getText();


        int validationCode;
        if (id.isEmpty() || name.isEmpty() || address.isEmpty() || tel.isEmpty() || email.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid studentname!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid email format!").show();
            case 3 -> new Alert(Alert.AlertType.ERROR, "Invalid address!").show();
            case 4 -> new Alert(Alert.AlertType.ERROR, "Invalid telephone number!").show();
            default -> {
                if (studentBo.StudentIdExists(id)){
                    new Alert(Alert.AlertType.ERROR, "Student ID " + id + " already exists!").show();
                    return;
                }
                if (studentBo.saveStudent(new StudentDTO(id, name, address, tel, email))) {
                    clearTextFileds();
                    generateNextUserId();
                    getAll();
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
            }
        }
    }

    private void clearTextFileds() {
        txtstudentid.clear();
        txtstudentname.clear();
        txtaddress.clear();
        txttel.clear();
        txtemail.clear();
        generateNextUserId();
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
            try {
                if (studentBo.findStudentById(ID).getEnrollmentList() != null && !studentBo.findStudentById(ID).getEnrollmentList().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "This student is enrolled in courses and cannot be deleted.").show();
                    return;
                }

                if (studentBo.deleteStudent(ID)) {
                    new Alert(Alert.AlertType.INFORMATION, "Student deleted successfully.").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete the student.").show();
                }
            } catch (NullPointerException e) {
                new Alert(Alert.AlertType.ERROR, "Student not found.").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Unexpected error occurred: " + e.getMessage()).show();
            }
        }
        generateNextUserId();
        clearTextFileds();
        getAll();
    }

    @FXML
    void btnsearchstudentOnAction(ActionEvent event) {
        String searchText = txtsearch.getText().toLowerCase();
        ObservableList<StudentTm> filteredList = FXCollections.observableArrayList();

        for (StudentTm studenttm : observableList) {
            if (studenttm.getSid().toLowerCase().contains(searchText)) {
                filteredList.add(studenttm);
            }
        }
        tblstudent.setItems(filteredList);
    }

    @FXML
    void btnupdateOnAction(ActionEvent event) throws Exception {
        String name = txtstudentname.getText();
        String address = txtaddress.getText();
        String tel = txttel.getText();
        String email = txtemail.getText();
        int validationCode;
        if (ID.isEmpty() || name.isEmpty() || address.isEmpty() || tel.isEmpty() || email.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid studentname!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid email format!").show();
            case 3 -> new Alert(Alert.AlertType.ERROR, "Invalid address!").show();
            case 4 -> new Alert(Alert.AlertType.ERROR, "Invalid telephone number!").show();
            default -> {
                if (studentBo.updateStudent(new StudentDTO(ID, name, address, tel, email))) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Update Successfully!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
                clearTextFileds();
                getAll();
            }
        }
    }
    public void rowOnMouseClicked(MouseEvent mouseEvent) {
        Integer index = tblstudent.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        ID = colstudentid.getCellData(index).toString();
        txtstudentid.setText(ID);
        txtstudentname.setText(colstudentname.getCellData(index).toString());
        txtaddress.setText(coladdress.getCellData(index).toString());
        txttel.setText(coltel.getCellData(index).toString());
        txtemail.setText(colemail.getCellData(index).toString());
    }
    @FXML
    void txtaddressOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.ADDRESS,txtaddress);
    }

    @FXML
    void txtemailOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.EMAIL,txtemail);
    }

    @FXML
    void txtsearchOnKeyReleased(KeyEvent event) {

    }

    @FXML
    void txtstudentidOnKeyReleased(KeyEvent event) {

    }

    @FXML
    void txtstudentnameOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtstudentname);
    }

    @FXML
    void txttelOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.TEL,txttel);
    }
    public int isValid() {
        if (!Regex.setTextColor(TextField.NAME, txtstudentname)) return 1;
        if (!Regex.setTextColor(TextField.EMAIL, txtemail)) return 2;
        if (!Regex.setTextColor(TextField.ADDRESS, txtaddress)) return 3;
        if (!Regex.setTextColor(TextField.TEL, txttel)) return 4;
        return 0;
    }

}
