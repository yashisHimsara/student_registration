package lk.Ijse.dao.custom;

import lk.Ijse.dao.CrudDAO;
import lk.Ijse.entity.User;

import java.sql.SQLException;

public interface UserDAO extends CrudDAO<User> {
    public User findUserById(String userId) throws Exception;
    public User findUserByname(String username) throws Exception;
    public boolean usernameExists(String username) throws SQLException, ClassNotFoundException;
}
