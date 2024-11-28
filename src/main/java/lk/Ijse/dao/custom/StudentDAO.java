package lk.Ijse.dao.custom;

import lk.Ijse.dao.CrudDAO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.Student;
import lk.Ijse.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface StudentDAO extends CrudDAO<Student> {
    public Student getStudentById(String studentId) throws Exception;
    public Student findStudentById(String studentId) throws Exception;
    public List<String> getAllStudentIds() throws SQLException, ClassNotFoundException;
    public int getStudentCount() throws SQLException, ClassNotFoundException;
}
