package lk.Ijse.dao.custom;

import lk.Ijse.dao.CrudDAO;
import lk.Ijse.entity.Enrollment;
import lk.Ijse.entity.Payment;

public interface PaymentDAO extends CrudDAO<Payment> {
    public Payment findPaymentById(String paymentId) throws Exception;
}
