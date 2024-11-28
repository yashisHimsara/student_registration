package lk.Ijse.bo.custom;

import lk.Ijse.bo.SuperBo;
import lk.Ijse.dto.PaymentDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.Payment;
import lk.Ijse.entity.Student;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface PaymentBo extends SuperBo {
    public boolean savePayment(PaymentDTO dto) throws Exception;
    public boolean updatePayment(PaymentDTO dto) throws Exception;
    public boolean deletePayment(String ID) throws Exception;
    public List<PaymentDTO> getAllPayment() throws SQLException, ClassNotFoundException;
    public String generateNewPaymentID() throws SQLException, ClassNotFoundException, IOException;
    public boolean PaymentIdExists(String PaymentId) throws SQLException, ClassNotFoundException;
    public List<String> getAllPaymentIds() throws SQLException, ClassNotFoundException;
    public Payment findPaymentById(String paymentId) throws Exception;

}
