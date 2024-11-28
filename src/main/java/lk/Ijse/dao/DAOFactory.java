package lk.Ijse.dao;

import lk.Ijse.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getDaoFactory() {
        return (daoFactory == null) ? daoFactory = new DAOFactory() : daoFactory;
    }

    public enum DaoType {
        User, Student, Payment, Course,Enrollment
    }

    public SuperDAO getDAO(DaoType daoType) {
        switch (daoType) {
            case User:
                return new UserDAOImpl();
            case Student:
                return new StudentDAOImpl();
            case Payment:
                return new PaymentDAOImpl();
            case Course:
                return new CourseDAOImpl();
            case Enrollment:
                return new EnrollmentDAOImpl();
            default:
                return null;
        }
    }

}
