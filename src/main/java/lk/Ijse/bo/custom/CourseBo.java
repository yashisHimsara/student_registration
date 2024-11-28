package lk.Ijse.bo.custom;

import lk.Ijse.bo.SuperBo;
import lk.Ijse.dto.CourseDTO;
import lk.Ijse.dto.StudentDTO;
import lk.Ijse.entity.Course;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface CourseBo extends SuperBo {
    public boolean saveCourse(CourseDTO dto) throws Exception;
    public boolean updateCourse(CourseDTO dto) throws Exception;
    public boolean deleteCourse(String ID) throws Exception;
    public List<CourseDTO> getAllCourse() throws SQLException, ClassNotFoundException;
    public String generateNewCourseID() throws SQLException, ClassNotFoundException, IOException;
    public boolean CourseIdExists(String courseId) throws SQLException, ClassNotFoundException;
    public List<String> getAllCourseIds() throws SQLException, ClassNotFoundException;
    public Course getCourseById(String courseId) throws Exception;
    public Course findCourseById(String courseId) throws Exception;
    public int getCourseCount() throws Exception;

}
