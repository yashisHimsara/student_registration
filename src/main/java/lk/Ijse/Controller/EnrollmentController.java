package lk.Ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import lk.Ijse.bo.custom.EnrollmentBo;
import lk.Ijse.bo.custom.StudentBo;
import lk.Ijse.bo.custom.impl.CourseBoImpl;
import lk.Ijse.bo.custom.impl.StudentBoImpl;
import lk.Ijse.dto.EnrollmentDTO;
import lk.Ijse.dto.PaymentDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.Enrollment;
import lk.Ijse.entity.Student;
import lk.Ijse.entity.tm.EnrollmentTm;
import lk.Ijse.entity.tm.PaymentTm;
import lk.Ijse.entity.tm.StudentTm;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class EnrollmentController implements Initializable {

    @FXML
    private JFXButton btnaddenrollment;

    @FXML
    private JFXButton btnclear1;

    @FXML
    private JFXButton btndelete;
    @FXML
    private JFXButton btnupdate;

    @FXML
    private JFXButton btnsearchcourse;

    @FXML
    private JFXButton btnsearchstudent;
    @FXML
    private JFXTextField txtsearchenrollment;

    @FXML
    private TableColumn<?, ?> colcomment;

    @FXML
    private TableColumn<?, ?> colcourse_id;

    @FXML
    private TableColumn<?, ?> colcoursename;

    @FXML
    private TableColumn<?, ?> coldate;

    @FXML
    private TableColumn<?, ?> colenrollmentid;

    @FXML
    private TableColumn<?, ?> colremain_fee;

    @FXML
    private TableColumn<?, ?> colstudentid;

    @FXML
    private TableColumn<?, ?> colstudentname;

    @FXML
    private TableColumn<?, ?> colupfront_fee;

    @FXML
    private AnchorPane enrollmentform;
    @FXML
    private JFXButton btnsearchenrollment;

    @FXML
    private TableView<EnrollmentTm> tblenrollment;

    @FXML
    private JFXTextField txtcomment;

    @FXML
    private JFXComboBox<String> txtcourseid;

    @FXML
    private JFXTextField txtcoursename;

    @FXML
    private JFXTextField txtdate;

    @FXML
    private JFXTextField txtenrollmentid;

    @FXML
    private JFXTextField txtsearchcourse;

    @FXML
    private JFXTextField txtsearchstudent;

    @FXML
    private JFXComboBox<String> txtstudentid;

    @FXML
    private JFXTextField txtstudentname;

    @FXML
    private JFXTextField txttotalfees;

    @FXML
    private JFXTextField txtupfrontpayment;


    ObservableList<EnrollmentTm> observableList;
    String ID;
    EnrollmentBo enrollmentBo = (EnrollmentBo) BoFactory.getBoFactory().getBo(BoFactory.BoType.Enrollment);
    StudentBo studentBo = (StudentBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Student);
    CourseBo courseBo = (CourseBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Course);



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            txtdate.setText(today.format(formatter));
            getAll();
            loadStudentIds();
            loadCourseIds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setCellValueFactory();
        generateNextUserId();
    }

    private void loadCourseIds() throws SQLException, ClassNotFoundException {
        List<String> courseIds = courseBo.getAllCourseIds();
        txtcourseid.getItems().clear();
        txtcourseid.getItems().addAll(courseIds);
    }

    private void generateNextUserId() {
        String nextId = null;
        try {
            nextId = enrollmentBo.generateNewEnrollmentID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        txtenrollmentid.setText(nextId);
    }

    private void setCellValueFactory() {
        colenrollmentid.setCellValueFactory(new PropertyValueFactory<>("eid"));
        colstudentid.setCellValueFactory(new PropertyValueFactory<>("sid"));
        colstudentname.setCellValueFactory(new PropertyValueFactory<>("Studentname"));
        colcourse_id.setCellValueFactory(new PropertyValueFactory<>("cid"));
        colcoursename.setCellValueFactory(new PropertyValueFactory<>("Coursename"));
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colupfront_fee.setCellValueFactory(new PropertyValueFactory<>("upfrontpayment"));
        colremain_fee.setCellValueFactory(new PropertyValueFactory<>("remainingfee"));
        colcomment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }

    private void getAll() throws SQLException, ClassNotFoundException {
        tblenrollment.getItems().clear();
        observableList = FXCollections.observableArrayList();
        List<EnrollmentDTO> allenrollment = enrollmentBo.getAllEnrollment();

        for (EnrollmentDTO enrollmentDTO : allenrollment){
            observableList.add(new EnrollmentTm(enrollmentDTO.getEid(),enrollmentDTO.getSid(),enrollmentDTO.getStudentname(),enrollmentDTO.getCid(),enrollmentDTO.getCoursename(),enrollmentDTO.getDate(),enrollmentDTO.getUpfrontpayment(),enrollmentDTO.getRemainingfee(),enrollmentDTO.getComment()));
        tblenrollment.setItems(observableList);
        }
    }

    private void loadStudentIds() throws Exception {
        List<String> studentIds = studentBo.getAllStudentIds();
        txtstudentid.getItems().clear();
        txtstudentid.getItems().addAll(studentIds);
    }

    @FXML
    private void handleCourseSelection(ActionEvent event) throws Exception {
        String selectedCourseId = txtcourseid.getValue();

        if (selectedCourseId != null && !selectedCourseId.isEmpty()) {
            Course selectedCourse = courseBo.getCourseById(selectedCourseId);

            if (selectedCourse != null) {
                String courseName = selectedCourse.getCoursename();
                Double fee = selectedCourse.getFee();
                System.out.println("Course ID: " + selectedCourse.getCid());
                System.out.println("Course Name: " + courseName);
                System.out.println("Course fee: " + fee);

                txtcoursename.setText(courseName);
                txttotalfees.setText(String.valueOf(fee));

            } else {
                System.out.println("Course not found for ID: " + selectedCourseId);
            }
        }
    }

    @FXML
    private void handleStudentSelection(ActionEvent event) throws Exception {
        String selectedStudentId = txtstudentid.getValue();

        if (selectedStudentId != null && !selectedStudentId.isEmpty()) {
            Student selectedStudent = studentBo.getStudentById(selectedStudentId);

            if (selectedStudent != null) {
                String studentName = selectedStudent.getName();
                System.out.println("Course ID: " + selectedStudent.getSid());
                System.out.println("Course Name: " + studentName);

                txtstudentname.setText(studentName);


            } else {
                System.out.println("Student not found for ID: " + selectedStudent);
            }
        }
    }


    @FXML
    void btnaddenrollmentOnAction(ActionEvent event) throws Exception {
        String id = txtenrollmentid.getText();
        String sid = txtstudentid.getValue();
        String studentname = txtstudentname.getText();
        String cid = txtcourseid.getValue();
        String coursename = txtcoursename.getText();
        LocalDate date = LocalDate.parse(txtdate.getText());
        Double totalfee = Double.valueOf(txttotalfees.getText());
        Double upfrontpayment = Double.valueOf(txtupfrontpayment.getText());
        Double remainingfee = totalfee - upfrontpayment;
        String comment = txtcomment.getText();

        int validationCode;
        if (id.isEmpty() || sid.isEmpty() || studentname.isEmpty() ||cid.isEmpty() ||coursename.isEmpty() ||date == null ||totalfee == null || upfrontpayment == null ||comment.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid comment!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid upfrontpayment!").show();
            default -> {
                if (enrollmentBo.EnrollmentIdExists(id)){
                    new Alert(Alert.AlertType.ERROR, "Enrollment ID " + id + " already exists!").show();
                    return;
                }

                if (enrollmentBo.isStudentEnrolledInCourse(sid, cid)) {
                    new Alert(Alert.AlertType.ERROR, "Student " + sid + " is already enrolled in course " + cid + "!").show();
                    return;
                }

                if (enrollmentBo.saveEnrollment(new EnrollmentDTO(id, sid, studentname, cid, coursename, date, upfrontpayment, remainingfee, comment))) {
                    clearTextFileds();
                    generateNextUserId();
                    getAll();
                    loadStudentIds();
                    loadCourseIds();
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
            }
        }
    }

    private void clearTextFileds() throws Exception {
        txtenrollmentid.clear();
        txtstudentid.getItems().clear();
        txtstudentname.clear();
        txtcourseid.getItems().clear();
        txtcoursename.clear();
        txtdate.clear();
        txtupfrontpayment.clear();
        txttotalfees.clear();
        txtcomment.clear();
        generateNextUserId();
        loadStudentIds();
        loadCourseIds();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        txtdate.setText(today.format(formatter));
    }

    @FXML
    void btnclearOnAction(ActionEvent event) throws Exception {
        clearTextFileds();
    }

    @FXML
    void btndeleteOnAction(ActionEvent event) throws Exception {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Optional<ButtonType> result = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

        if (result.orElse(no) == yes) {
            if (!enrollmentBo.deleteEnrollment(ID)) {
                new Alert(Alert.AlertType.ERROR, "Error!!").show();
            }
        }
        clearTextFileds();
        generateNextUserId();
        getAll();
        loadStudentIds();
        loadCourseIds();
    }


    @FXML
    void btnupdateOnAction(ActionEvent event) throws Exception {
        String id = txtenrollmentid.getText();
        String sid = txtstudentid.getValue();
        String studentname = txtstudentname.getText();
        String cid = txtcourseid.getValue();
        String coursename = txtcoursename.getText();
        LocalDate date = LocalDate.parse(txtdate.getText());
        Double upfrontpayment = Double.valueOf(txtupfrontpayment.getText());
        Enrollment enrollmentById = enrollmentBo.findEnrollmentById(id);
        Double newremainfeecalculate = newremainfeecalculate(enrollmentById, upfrontpayment);
        String comment = txtcomment.getText();
        int validationCode;
        if (id.isEmpty() || sid.isEmpty() || studentname.isEmpty() ||cid.isEmpty() ||coursename.isEmpty() ||date == null || upfrontpayment == null ||comment.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid comment!").show();
            case 2 -> new Alert(Alert.AlertType.ERROR, "Invalid upfrontpayment!").show();
            default -> {
                if (enrollmentBo.updateEnrollment(new EnrollmentDTO(id, sid, studentname, cid, coursename, date, upfrontpayment, newremainfeecalculate, comment))) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Update Successfully!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
                clearTextFileds();
                generateNextUserId();
                getAll();
                loadStudentIds();
                loadCourseIds();
            }
        }
    }

    private Double newremainfeecalculate(Enrollment enrollment,Double newupfrontpayment) {
        Double upfrontpayment = enrollment.getUpfrontpayment();
        Double remainingfee = enrollment.getRemainingfee();
        double fee = (remainingfee + upfrontpayment) - newupfrontpayment;
        return fee;
    }

    @FXML
    void btnsearchcourseOnAction(ActionEvent event) {
        String searchText = txtsearchcourse.getText().toLowerCase();
        ObservableList<EnrollmentTm> filteredList = FXCollections.observableArrayList();

        for (EnrollmentTm enrollmentTm : observableList) {
            if (enrollmentTm.getCid().toLowerCase().contains(searchText)) {
                filteredList.add(enrollmentTm);
            }
        }
        tblenrollment.setItems(filteredList);
    }

    @FXML
    void btnsearchstudentOnAction(ActionEvent event) {
        String searchText = txtsearchstudent.getText().toLowerCase();
        ObservableList<EnrollmentTm> filteredList = FXCollections.observableArrayList();

        for (EnrollmentTm enrollmentTm : observableList) {
            if (enrollmentTm.getSid().toLowerCase().contains(searchText)) {
                filteredList.add(enrollmentTm);
            }
        }
        tblenrollment.setItems(filteredList);
    }
    @FXML
    void txtsearchenrollmentOnAction(ActionEvent event) {

    }
    @FXML
    void btnsearchsenrollmentOnAction(ActionEvent event) {
        String searchText = txtsearchenrollment.getText().toLowerCase();
        ObservableList<EnrollmentTm> filteredList = FXCollections.observableArrayList();

        for (EnrollmentTm enrollmentTm : observableList) {
            if (enrollmentTm.getEid().toLowerCase().contains(searchText)) {
                filteredList.add(enrollmentTm);
            }
        }
        tblenrollment.setItems(filteredList);
    }

    public void rowOnMouseClicked(MouseEvent mouseEvent) throws Exception {
        Integer index = tblenrollment.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        ID = colenrollmentid.getCellData(index).toString();
        txtenrollmentid.setText(ID);
        txtstudentid.setValue((String) colstudentid.getCellData(index));
        txtstudentname.setText(colstudentname.getCellData(index).toString());
        txtcourseid.setValue(colcourse_id.getCellData(index).toString());
        txtcoursename.setText(colcoursename.getCellData(index).toString());
        txtdate.setText(coldate.getCellData(index).toString());
        txtupfrontpayment.setText(String.valueOf(colupfront_fee.getCellData(index)));
        String id = colcourse_id.getCellData(index).toString();
        Double fee = courseBo.findCourseById(id).getFee();
        txttotalfees.setText(String.valueOf(fee));
        txtcomment.setText((String) colcomment.getCellData(index));
    }

    @FXML
    void txtcommentOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.NAME,txtcomment);
    }

    @FXML
    void txtupfrontpaymentOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PRICE,txtupfrontpayment);
    }
    public int isValid() {
        if (!Regex.setTextColor(TextField.NAME, txtcomment)) return 1;
        if (!Regex.setTextColor(TextField.PRICE, txtupfrontpayment)) return 2;
        return 0;
    }

}
