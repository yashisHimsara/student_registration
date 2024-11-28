package lk.Ijse.dao.custom.impl;

import lk.Ijse.config.FactoryConfiguration;
import lk.Ijse.dao.custom.EnrollmentDAO;
import lk.Ijse.entity.Course;
import lk.Ijse.entity.Enrollment;
import lk.Ijse.entity.Student;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {
    @Override
    public boolean save(Enrollment entity) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction tx = session.beginTransaction();
        session.save(entity);

        Student student = entity.getStudent();
        Course course = entity.getCourse();
        if (student != null) {
            student.addEnrollment(entity);
        }
        if (course != null) {
            course.addEnrollment(entity);
        }

        tx.commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(Enrollment entity) throws Exception {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Transaction tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
            return true;
        }
    }

    @Override
    public boolean delete(String ID) throws Exception {
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction tx = session.beginTransaction();

        try {
            Enrollment enrollment = session.get(Enrollment.class, ID);
            if (enrollment != null) {
                Student student = enrollment.getStudent();
                Course course = enrollment.getCourse();

                if (student != null) {
                    student.removeEnrollment(enrollment);
                }
                if (course != null) {
                    course.removeEnrollment(enrollment);
                }

                session.delete(enrollment);
                tx.commit();
                return true;
            } else {
                tx.rollback();
                return false;
            }
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public List<Enrollment> getAll() throws SQLException, ClassNotFoundException {
        List<Enrollment> all = new ArrayList<>();
        Session session = FactoryConfiguration.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        all = session.createQuery("from Enrollment", Enrollment.class).list();
        transaction.commit();
        session.close();
        return all;
    }

    @Override
    public String generateNewID() throws SQLException, ClassNotFoundException, IOException {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            String lastID = (String) session.createQuery("SELECT e.eid FROM Enrollment e ORDER BY e.eid DESC")
                    .setMaxResults(1)
                    .uniqueResult();

            if (lastID != null) {
                int id = Integer.parseInt(lastID.replace("E", "")) + 1;
                return "E" + String.format("%03d", id);
            } else {
                return "E001";
            }
        }
    }

    @Override
    public boolean IdExists(String id) throws SQLException, ClassNotFoundException {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            String hql = "SELECT COUNT(e.eid) FROM Enrollment e WHERE e.eid = :id";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("id", id);
            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public List<String> getAllEnrollmentIds() throws SQLException, ClassNotFoundException {
        List<String> enrollmentIds = new ArrayList<>();
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<String> query = session.createQuery("SELECT e.eid FROM Enrollment e", String.class);
            enrollmentIds = query.list();
        }
        return enrollmentIds;
    }

    public boolean isStudentEnrolledInCourse(String studentId, String courseId) throws Exception {
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            String hql = "SELECT COUNT(e) FROM Enrollment e WHERE e.student.sid = :studentId AND e.course.cid = :courseId";
            Long count = (Long) session.createQuery(hql)
                    .setParameter("studentId", studentId)
                    .setParameter("courseId", courseId)
                    .uniqueResult();
            return count > 0; // true if the student is already enrolled
        }
    }

    @Override
    public Enrollment findEnrollmentById(String enrollmentId) throws Exception {
        Transaction transaction = null;
        Enrollment enrollment = null;

        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            transaction = session.beginTransaction();

            Query<Enrollment> query = session.createQuery("FROM Enrollment e WHERE e.eid = :eid", Enrollment.class);
            query.setParameter("eid", enrollmentId);

            enrollment = query.uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new Exception("Error finding enrollment with ID: " + enrollmentId, e);
        }

        return enrollment;
    }





    @Override
    public double getRemainingFeeByEnrollmentId(String enrollmentId) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;
        Double remainFee = null;

        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            transaction = session.beginTransaction();

            Query<Enrollment> query = session.createQuery("FROM Enrollment e WHERE e.id = :id", Enrollment.class);
            query.setParameter("id", enrollmentId);
            Enrollment enrollment = query.uniqueResult();

            transaction.commit();


            if (enrollment != null) {
                remainFee = enrollment.getRemainingfee();
            } else {
                throw new Exception("Enrollment ID not found!");
            }
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            try {
                throw e;
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return remainFee;
    }

    @Override
    public boolean updateRemainingFee(String enrollmentId, double newFee) throws SQLException, ClassNotFoundException {
        Transaction transaction = null;

        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            transaction = session.beginTransaction();


            Query query = session.createQuery("UPDATE Enrollment e SET e.remainingfee = :newFee WHERE e.id = :id");
            query.setParameter("newFee", newFee);
            query.setParameter("id", enrollmentId);

            int result = query.executeUpdate();

            transaction.commit();

            return result > 0;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }


    @Override
    public int getEnrollmentCount() throws SQLException, ClassNotFoundException {
        int enrollmentCount = 0;
        try (Session session = FactoryConfiguration.getInstance().getSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(e) FROM Enrollment e", Long.class);
            enrollmentCount = query.uniqueResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Failed to fetch enrollment count from the database", e);
        }
        return enrollmentCount;
    }

}
