package lk.Ijse.bo.custom;

import lk.Ijse.bo.SuperBo;
import lk.Ijse.dto.EnrollmentDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.Enrollment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface EnrollmentBo extends SuperBo {
    public boolean saveEnrollment(EnrollmentDTO dto) throws Exception;
    public boolean updateEnrollment(EnrollmentDTO dto) throws Exception;
    public boolean deleteEnrollment(String ID) throws Exception;
    public List<EnrollmentDTO> getAllEnrollment() throws SQLException, ClassNotFoundException;
    public String generateNewEnrollmentID() throws SQLException, ClassNotFoundException, IOException;
    public boolean EnrollmentIdExists(String enrollmentId) throws SQLException, ClassNotFoundException;
    public List<String> getAllEnrollmentIds() throws SQLException, ClassNotFoundException;
    public boolean isStudentEnrolledInCourse(String studentId, String courseId) throws Exception;
    public Enrollment findEnrollmentById(String enrollmentId) throws Exception;
    public double getRemainingFeeByEnrollmentId(String enrollmentId) throws SQLException, ClassNotFoundException;
    public boolean updateRemainingFee(String enrollmentId, double newFee) throws SQLException, ClassNotFoundException;
    public int getEnrollmentCount() throws Exception;

}
