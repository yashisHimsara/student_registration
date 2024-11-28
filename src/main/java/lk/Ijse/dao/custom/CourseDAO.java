package lk.Ijse.dao.custom;

import lk.Ijse.dao.CrudDAO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.User;

import java.sql.SQLException;
import java.util.List;

public interface CourseDAO extends CrudDAO<Course> {
    public Course getCourseById(String courseId) throws Exception;
    public Course findCourseById(String courseId) throws Exception;
    public List<String> getAllCourseIds() throws SQLException, ClassNotFoundException;
    public int getCourseCount() throws SQLException, ClassNotFoundException;
}
