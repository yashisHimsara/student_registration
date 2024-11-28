package lk.Ijse.bo;

import lk.Ijse.bo.custom.impl.*;

public class BoFactory {
    private static BoFactory boFactory;
    private BoFactory() {

    }

    public static BoFactory getBoFactory() {
        return (boFactory == null) ? boFactory = new BoFactory() : boFactory;
    }
    public enum BoType{
        User, Student, Payment, Course, Enrollment

    }
    public SuperBo getBo(BoType boType){
        switch (boType){

            case User:
                return new UserBoImpl();
            case Student:
                return new StudentBoImpl();
            case Payment:
                return  new PaymentBoImpl();
            case Course:
                return  new CourseBoImpl();
            case Enrollment:
                return  new EnrollmentBoImpl();
            default:
                return null;

        }
    }
}
