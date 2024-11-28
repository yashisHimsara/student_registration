package lk.Ijse.bo.custom.impl;

import lk.Ijse.bo.custom.CourseBo;
import lk.Ijse.dao.DAOFactory;
import lk.Ijse.dao.custom.CourseDAO;
import lk.Ijse.dto.CourseDTO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.Student;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseBoImpl implements CourseBo {
    CourseDAO courseDAO = (CourseDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DaoType.Course);
    @Override
    public boolean saveCourse(CourseDTO dto) throws Exception {
        return courseDAO.save(new Course(dto.getCid(),dto.getCoursename(),dto.getDuration(),dto.getFee()));

    }

    @Override
    public boolean updateCourse(CourseDTO dto) throws Exception {
        return courseDAO.update(new Course(dto.getCid(),dto.getCoursename(),dto.getDuration(),dto.getFee()));
    }

    @Override
    public boolean deleteCourse(String ID) throws Exception {
        return courseDAO.delete(ID);
    }

    @Override
    public List<CourseDTO> getAllCourse() throws SQLException, ClassNotFoundException {
        List<Course> courses = courseDAO.getAll();
        List<CourseDTO> dtos = new ArrayList<>();
        for (Course course : courses) {
            dtos.add(new CourseDTO(course.getCid(),course.getCoursename(),course.getDuration(),course.getFee()));
        }
        return dtos;
    }

    @Override
    public String generateNewCourseID() throws SQLException, ClassNotFoundException, IOException {
        return courseDAO.generateNewID();
    }

    @Override
    public boolean CourseIdExists(String courseId) throws SQLException, ClassNotFoundException {
        return courseDAO.IdExists(courseId);
    }

    @Override
    public List<String> getAllCourseIds() throws SQLException, ClassNotFoundException {
        List<String> courseIds = new ArrayList<>();
        List<Course> courses = courseDAO.getAll();
        for (Course course : courses) {
            courseIds.add(course.getCid());
        }
        return courseIds;
    }

    @Override
    public Course getCourseById(String courseId) throws Exception {
        return courseDAO.getCourseById(courseId);
    }

    @Override
    public Course findCourseById(String courseId) throws Exception {
        return courseDAO.findCourseById(courseId);
    }
    public int getCourseCount() throws Exception {
        return courseDAO.getCourseCount();
    }

}
