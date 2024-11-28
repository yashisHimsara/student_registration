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
import lk.Ijse.Utill.Regex;
import lk.Ijse.Utill.TextField;
import lk.Ijse.bo.BoFactory;
import lk.Ijse.bo.custom.CourseBo;
import lk.Ijse.bo.custom.StudentBo;
import lk.Ijse.bo.custom.impl.CourseBoImpl;
import lk.Ijse.bo.custom.impl.StudentBoImpl;
import lk.Ijse.dto.CourseDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.tm.CourseTm;
import lk.Ijse.entity.tm.StudentTm;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class CourseController implements Initializable {

    @FXML
    private JFXButton btnaddcourse;

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnsearchcourse;

    @FXML
    private JFXButton btnupdate;

    @FXML
    private TableColumn<?, ?> colcourseid;

    @FXML
    private TableColumn<?, ?> colcoursename;

    @FXML
    private TableColumn<?, ?> colduration;

    @FXML
    private TableColumn<?, ?> colfees;

    @FXML
    private AnchorPane courseform;

    @FXML
    private TableView<CourseTm> tblcourse;

    @FXML
    private JFXTextField txtcoursename;

    @FXML
    private JFXTextField txtduration;

    @FXML
    private JFXTextField txtfees;

    @FXML
    private JFXTextField txtscourseid;

    @FXML
    private JFXTextField txtsearch;

    ObservableList<CourseTm> observableList;
    String ID;
    CourseBo courseBo = (CourseBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Course);

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
            nextId = courseBo.generateNewCourseID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        txtscourseid.setText(nextId);
    }

    private void setCellValueFactory() {
        colcourseid.setCellValueFactory(new PropertyValueFactory<>("cid"));
        colcoursename.setCellValueFactory(new PropertyValueFactory<>("coursename"));
        colduration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colfees.setCellValueFactory(new PropertyValueFactory<>("fee"));
    }

    private void getAll() throws SQLException, ClassNotFoundException {
        observableList = FXCollections.observableArrayList();
        List<CourseDTO> allCourse = courseBo.getAllCourse();

        for (CourseDTO courseDTO: allCourse){
            observableList.add(new CourseTm(courseDTO.getCid(),courseDTO.getCoursename(),courseDTO.getDuration(),courseDTO.getFee()));
        }
        tblcourse.setItems(observableList);
    }

    @FXML
    void btnaddcourseOnAction(ActionEvent event) throws Exception {
        String id = txtscourseid.getText();
        String name = txtcoursename.getText();
        String duration = txtduration.getText();
        Double fees = Double.valueOf(txtfees.getText());


        int validationCode;
        if (id.isEmpty() || name.isEmpty() || duration.isEmpty() || fees == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid coursename!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid duration!").show();
            case 3 -> new Alert(Alert.AlertType.ERROR, "Invalid fees!").show();
            default -> {
                if (courseBo.CourseIdExists(id)){
                    new Alert(Alert.AlertType.ERROR, "Course ID " + id + " already exists!").show();
                    return;
                }
                if (courseBo.saveCourse(new CourseDTO(id, name, duration, fees))) {
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
        txtscourseid.clear();
        txtcoursename.clear();
        txtduration.clear();
        txtfees.clear();
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
                if (courseBo.findCourseById(ID).getEnrollmentList() != null &&
                        !courseBo.findCourseById(ID).getEnrollmentList().isEmpty()) {
                    new Alert(Alert.AlertType.WARNING, "This course is enrolled by students, so it cannot be removed.").show();
                    return;
                }

                if (!courseBo.deleteCourse(ID)) {
                    new Alert(Alert.AlertType.ERROR, "Error occurred while deleting the course.").show();
                } else {
                    new Alert(Alert.AlertType.INFORMATION, "Course deleted successfully.").show();
                }
            } catch (NullPointerException e) {
                new Alert(Alert.AlertType.ERROR, "Course not found.").show();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Unexpected error: " + e.getMessage()).show();
            }
        }
        generateNextUserId();
        clearTextFileds();
        getAll();
    }

    @FXML
    void btnsearchcourseOnAction(ActionEvent event) {
        String searchText = txtsearch.getText().toLowerCase();
        ObservableList<CourseTm> filteredList = FXCollections.observableArrayList();

        for (CourseTm courseTm : observableList) {
            if (courseTm.getCid().toLowerCase().contains(searchText)) {
                filteredList.add(courseTm);
            }
        }
        tblcourse.setItems(filteredList);
    }

    @FXML
    void btnupdateOnAction(ActionEvent event) throws Exception {
        String name = txtcoursename.getText();
        String duration = txtduration.getText();
        Double fees = Double.valueOf(txtfees.getText());
        int validationCode;
        if (ID.isEmpty() || name.isEmpty() || duration.isEmpty() || fees == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid coursename!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid duration!").show();
            case 3 -> new Alert(Alert.AlertType.ERROR, "Invalid fees!").show();
            default -> {
                if (courseBo.updateCourse(new CourseDTO(ID, name, duration, fees))) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Update Successfully!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
                clearTextFileds();
                getAll();
            }
        }
    }

    @FXML
    void rowOnMouseClicked(MouseEvent event) {
        Integer index = tblcourse.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        ID = colcourseid.getCellData(index).toString();
        txtscourseid.setText(ID);
        txtcoursename.setText(colcoursename.getCellData(index).toString());
        txtduration.setText(colduration.getCellData(index).toString());
        txtfees.setText(colfees.getCellData(index).toString());
    }

    @FXML
    void txtcoursenameOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtcoursename);
    }

    @FXML
    void txtdurationOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.DURATION,txtduration);
    }

    @FXML
    void txtfeesOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PRICE,txtfees);
    }

    @FXML
    void txtscourseidOnKeyReleased(KeyEvent event) {

    }

    public int isValid() {
        if (!Regex.setTextColor(TextField.NAME, txtcoursename)) return 1;
        if (!Regex.setTextColor(TextField.DURATION, txtduration)) return 2;
        if (!Regex.setTextColor(TextField.PRICE, txtfees)) return 3;
        return 0;
    }


}
