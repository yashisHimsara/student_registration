package lk.Ijse.bo.custom.impl;

import lk.Ijse.bo.custom.UserBo;
import lk.Ijse.dao.DAOFactory;
import lk.Ijse.dao.custom.UserDAO;
import lk.Ijse.dto.UserDTO;
import lk.Ijse.entity.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserBoImpl implements UserBo {

    UserDAO userDAO = (UserDAO) DAOFactory.getDaoFactory().getDAO(DAOFactory.DaoType.User);

    @Override
    public boolean saveUser(UserDTO dto) throws Exception {
        return userDAO.save(new User(dto.getUserid(),dto.getUsername(),dto.getPassword(),dto.getEmail(),dto.getRole()));
    }

    @Override
    public boolean updateUser(UserDTO dto) throws Exception {
        return userDAO.update(new User(dto.getUserid(),dto.getUsername(),dto.getPassword(),dto.getEmail(),dto.getRole()));
    }

    @Override
    public boolean deleteUser(String ID) throws Exception {
        return userDAO.delete(ID);
    }

    @Override
    public List<UserDTO> getAllUser() throws SQLException, ClassNotFoundException {
        List<User> users = userDAO.getAll();
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(new UserDTO(user.getUserid(),user.getUsername(),user.getPassword(),user.getEmail(),user.getRole()));
        }
        return dtos;
    }

    @Override
    public String generateNewUserID() throws SQLException, ClassNotFoundException, IOException {
        return userDAO.generateNewID();
    }

    @Override
    public boolean UserIdExists(String userId) throws SQLException, ClassNotFoundException {
        return userDAO.IdExists(userId);
    }

    @Override
    public User findUserById(String userId) throws Exception {
        return userDAO.findUserById(userId);
    }

    @Override
    public User findUserByname(String username) throws Exception {
        return userDAO.findUserByname(username);
    }

    @Override
    public boolean usernameExists(String username) throws SQLException, ClassNotFoundException {
        return userDAO.usernameExists(username);
    }
}
