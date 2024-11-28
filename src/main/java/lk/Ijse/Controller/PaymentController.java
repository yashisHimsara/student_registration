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
import lk.Ijse.bo.custom.EnrollmentBo;
import lk.Ijse.bo.custom.PaymentBo;
import lk.Ijse.bo.custom.StudentBo;
import lk.Ijse.bo.custom.impl.EnrollmentBoImpl;
import lk.Ijse.bo.custom.impl.PaymentBoImpl;
import lk.Ijse.bo.custom.impl.StudentBoImpl;
import lk.Ijse.dto.PaymentDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.tm.EnrollmentTm;
import lk.Ijse.entity.tm.PaymentTm;
import lk.Ijse.entity.tm.StudentTm;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private JFXButton btnaddpayment;

    @FXML
    private JFXButton btnclear;

    @FXML
    private JFXButton btndelete;

    @FXML
    private JFXButton btnsearchpayment;

    @FXML
    private JFXButton btnupdate;

    @FXML
    private TableColumn<?, ?> colamount;

    @FXML
    private TableColumn<?, ?> coldate;

    @FXML
    private TableColumn<?, ?> colenrollmentid;

    @FXML
    private TableColumn<?, ?> colpaymentid;

    @FXML
    private AnchorPane paymentform;

    @FXML
    private TableView<PaymentTm> tblpayment;

    @FXML
    private JFXTextField txtamount;

    @FXML
    private JFXTextField txtdate;

    @FXML
    private JFXComboBox<String> txtenrollmentid;

    @FXML
    private JFXTextField txtpaymentid;

    @FXML
    private JFXTextField txtsearch;
    ObservableList<PaymentTm> observableList;
    String ID;
    PaymentBo paymentBo = (PaymentBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Payment);
    EnrollmentBo enrollmentBo = (EnrollmentBoImpl) BoFactory.getBoFactory().getBo(BoFactory.BoType.Enrollment);


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            txtdate.setText(today.format(formatter));
            getAll();
            loadEnrollmentIds();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        setCellValueFactory();
        generateNextPaymentId();
    }

    private void loadEnrollmentIds() throws SQLException, ClassNotFoundException {
        List<String> enrollmentIds = enrollmentBo.getAllEnrollmentIds();
        txtenrollmentid.getItems().clear();
        txtenrollmentid.getItems().addAll(enrollmentIds);
    }

    private void generateNextPaymentId() {
        String nextId = null;
        try {
            nextId = paymentBo.generateNewPaymentID();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        txtpaymentid.setText(nextId);
    }

    private void setCellValueFactory() {
        colpaymentid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colamount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colenrollmentid.setCellValueFactory(new PropertyValueFactory<>("eid"));
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void getAll() throws SQLException, ClassNotFoundException {
        observableList = FXCollections.observableArrayList();
        List<PaymentDTO> allPayment = paymentBo.getAllPayment();

        for (PaymentDTO paymentDTO : allPayment){
            observableList.add(new PaymentTm(paymentDTO.getId(),paymentDTO.getAmount(),paymentDTO.getEid(),paymentDTO.getDate()));
        }
        tblpayment.setItems(observableList);
    }

    @FXML
    void btnaddpaymentOnAction(ActionEvent event) throws Exception {
        String id = txtpaymentid.getText();
        String eid = txtenrollmentid.getValue();
        Double amount = Double.valueOf(txtamount.getText());
        LocalDate date = LocalDate.parse(txtdate.getText());


        int validationCode;
        if (id.isEmpty() || eid.isEmpty() || amount == null || date == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid amount!").show();
            default -> {
                if (paymentBo.PaymentIdExists(id)){
                    new Alert(Alert.AlertType.ERROR, "Payment ID " + id + " already exists!").show();
                    return;
                }

                if(amount > (enrollmentBo.findEnrollmentById(eid).getRemainingfee())){
                    new Alert(Alert.AlertType.ERROR, "Payment exceeds the remaining fee. Please enter a valid amount!").show();
                    return;
                }
                if (paymentBo.savePayment(new PaymentDTO(id, eid, amount, date))) {
                    updateremainfee();
                    clearTextFileds();
                    loadEnrollmentIds();
                    generateNextPaymentId();
                    getAll();
                    new Alert(Alert.AlertType.CONFIRMATION, "Saved!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
            }
        }
    }

    private void updateremainfee() {
        try {
            String eid = txtenrollmentid.getValue();
            double paymentAmount = Double.parseDouble(txtamount.getText());

            double currentRemainFee = enrollmentBo.getRemainingFeeByEnrollmentId(eid);

            double updatedRemainFee = currentRemainFee - paymentAmount;

            boolean isUpdated = enrollmentBo.updateRemainingFee(eid, updatedRemainFee);

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Remaining fee updated successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update remaining fee!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error updating remaining fee: " + e.getMessage()).show();
        }
    }

    private void clearTextFileds() {
        txtpaymentid.clear();
        txtenrollmentid.getItems().clear();
        txtamount.clear();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        txtdate.setText(today.format(formatter));
        generateNextPaymentId();
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
            if (!paymentBo.deletePayment(ID)) {
                new Alert(Alert.AlertType.ERROR, "Error!!").show();
            }
        }
        clearTextFileds();
        generateNextPaymentId();
        getAll();
        loadEnrollmentIds();
    }

    @FXML
    void btnsearchpaymentOnAction(ActionEvent event) {
        String searchText = txtsearch.getText().toLowerCase();
        ObservableList<PaymentTm> filteredList = FXCollections.observableArrayList();

        for (PaymentTm paymentTm : observableList) {
            if (paymentTm.getId().toLowerCase().contains(searchText)) {
                filteredList.add(paymentTm);
            }
        }
       tblpayment.setItems(filteredList);
    }

    @FXML
    void btnupdateOnAction(ActionEvent event) throws Exception {
        String id = txtpaymentid.getText();
        String eid = txtenrollmentid.getValue();
        Double amount = Double.valueOf(txtamount.getText());
        LocalDate date = LocalDate.parse(txtdate.getText());
        Double previousamount = paymentBo.findPaymentById(id).getAmount();

        int validationCode;
        if (id.isEmpty() || eid.isEmpty() || amount == null || date == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill in all fields!").show();
            return;
        }else {
            validationCode = isValid();
        }
        switch (validationCode) {
            case 1 -> new Alert(Alert.AlertType.ERROR, "Invalid amount!").show();
            default -> {

                if(amount > (enrollmentBo.findEnrollmentById(eid).getRemainingfee())){
                    new Alert(Alert.AlertType.ERROR, "Payment exceeds the remaining fee. Please enter a valid amount!").show();
                    return;
                }
                if (paymentBo.updatePayment(new PaymentDTO(id, eid, amount, date))) {
                    updateremainfees(id, amount, previousamount);
                    new Alert(Alert.AlertType.CONFIRMATION, "Update Successfully!!").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Error!!").show();
                }
                clearTextFileds();
                loadEnrollmentIds();
                getAll();
            }
        }
    }
                                                                //90 - 10 =80              90 -10 = 80
                                                                //90-20 = 70               90 -5 = 85
                                                                //80 - 10 = 70             80 +5 = 85
                                                                //80 - (20-10) = 70        80 - (5 - 10) = 85
    private void updateremainfees(String id,Double amount,Double previousamount) {
        try {
            String eid = txtenrollmentid.getValue();

            double currentRemainFee = enrollmentBo.getRemainingFeeByEnrollmentId(eid);

            double updatedRemainFee = currentRemainFee - (amount - previousamount);
            System.out.println("Current Remaining Fee: " + currentRemainFee);
            System.out.println("Previous Payment Amount: " + previousamount);
            System.out.println("New Payment Amount: " + amount);

            boolean isUpdated = enrollmentBo.updateRemainingFee(eid, updatedRemainFee);

            if (isUpdated) {
                new Alert(Alert.AlertType.INFORMATION, "Remaining fee updated successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update remaining fee!").show();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error updating remaining fee: " + e.getMessage()).show();
        }
    }

    public void rowOnMouseClicked(MouseEvent mouseEvent) {
        Integer index = tblpayment.getSelectionModel().getSelectedIndex();
        if (index <= -1) {
            return;
        }
        ID = colpaymentid.getCellData(index).toString();
        txtpaymentid.setText(ID);
        txtenrollmentid.setValue((String) colenrollmentid.getCellData(index));
        txtamount.setText(colamount.getCellData(index).toString());
        txtdate.setText(coldate.getCellData(index).toString());
    }

    @FXML
    void txtamountOnKeyReleased(KeyEvent event) {
        Regex.setTextColor(TextField.PRICE,txtamount);
    }
    public int isValid() {
        if (!Regex.setTextColor(TextField.PRICE, txtamount)) return 1;
        return 0;
    }
}
