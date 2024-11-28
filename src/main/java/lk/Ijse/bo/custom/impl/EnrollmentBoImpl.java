package lk.Ijse.bo.custom.impl;

import lk.Ijse.bo.custom.EnrollmentBo;
import lk.Ijse.dao.DAOFactory;
import lk.Ijse.dao.custom.CourseDAO;
import lk.Ijse.dao.custom.EnrollmentDAO;
import lk.Ijse.dao.custom.StudentDAO;
import lk.Ijse.dto.EnrollmentDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.Enrollment;
import lk.Ijse.entity.Student;
import lk.Ijse.entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentBoImpl implements EnrollmentBo {
    EnrollmentDAO enrollmentDAO = (EnrollmentDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DaoType.Enrollment);
    StudentDAO studentDAO = (StudentDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DaoType.Student);
    CourseDAO courseDAO = (CourseDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DaoType.Course);

    @Override
    public boolean saveEnrollment(EnrollmentDTO dto) throws Exception {

        Student student = studentDAO.findStudentById(dto.getSid());
        Course course = courseDAO.findCourseById(dto.getCid());


        if (student == null || course == null) {
            throw new Exception("Student or Course not found.");
        }

        Enrollment enrollment = new Enrollment(
                dto.getEid(),
                student,
                course,
                dto.getDate(),
                dto.getUpfrontpayment(),
                dto.getRemainingfee(),
                dto.getComment()
        );

        return enrollmentDAO.save(enrollment);
    }

    @Override
    public boolean updateEnrollment(EnrollmentDTO dto) throws Exception {
        Student student = studentDAO.findStudentById(dto.getSid());
        Course course = courseDAO.findCourseById(dto.getCid());

        if (student == null || course == null) {
            throw new Exception("Student or Course not found.");
        }

        Enrollment enrollment = new Enrollment(
                dto.getEid(),
                student,
                course,
                dto.getDate(),
                dto.getUpfrontpayment(),
                dto.getRemainingfee(),
                dto.getComment()
        );


        return enrollmentDAO.update(enrollment);
    }

    @Override
    public boolean deleteEnrollment(String ID) throws Exception {
        return enrollmentDAO.delete(ID);
    }

    @Override
    public List<EnrollmentDTO> getAllEnrollment() throws SQLException, ClassNotFoundException {
        List<Enrollment> enrollments = enrollmentDAO.getAll();
        List<EnrollmentDTO> dtos = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            String studentId = enrollment.getStudent() != null ? enrollment.getStudent().getSid() : null;
            String studentName = enrollment.getStudent() != null ? enrollment.getStudent().getName() : null;
            String courseId = enrollment.getCourse() != null ? enrollment.getCourse().getCid() : null;
            String courseName = enrollment.getCourse() != null ? enrollment.getCourse().getCoursename() : null;
            dtos.add(new EnrollmentDTO(enrollment.getEid(),studentId,studentName,courseId,courseName,enrollment.getDate(),enrollment.getUpfrontpayment(),enrollment.getRemainingfee(),enrollment.getComment()));
        }
        return dtos;
    }

    @Override
    public String generateNewEnrollmentID() throws SQLException, ClassNotFoundException, IOException {
        return enrollmentDAO.generateNewID();
    }

    @Override
    public boolean EnrollmentIdExists(String enrollmentId) throws SQLException, ClassNotFoundException {
        return enrollmentDAO.IdExists(enrollmentId);
    }

    @Override
    public List<String> getAllEnrollmentIds() throws SQLException, ClassNotFoundException {
        List<String> enrollmentIds = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentDAO.getAll();
        for (Enrollment enrollment : enrollments) {
            enrollmentIds.add(enrollment.getEid());
        }
        return enrollmentIds;
    }

    @Override
    public boolean isStudentEnrolledInCourse(String studentId, String courseId) throws Exception {
        return enrollmentDAO.isStudentEnrolledInCourse(studentId,courseId);
    }

    @Override
    public Enrollment findEnrollmentById(String enrollmentId) throws Exception {
        return enrollmentDAO.findEnrollmentById(enrollmentId);
    }

    @Override
    public double getRemainingFeeByEnrollmentId(String enrollmentId) throws SQLException, ClassNotFoundException {
        return enrollmentDAO.getRemainingFeeByEnrollmentId(enrollmentId);
    }

    @Override
    public boolean updateRemainingFee(String enrollmentId, double newFee) throws SQLException, ClassNotFoundException {
        return enrollmentDAO.updateRemainingFee(enrollmentId,newFee);
    }
    @Override
    public int getEnrollmentCount() throws Exception {
        return enrollmentDAO.getEnrollmentCount();
    }


}
