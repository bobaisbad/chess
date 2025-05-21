package dataaccess;

import model.UserData;
import request.RegisterRequest;

public interface UserDAO {

    void createUser(RegisterRequest req) throws DataAccessException;

    void deleteUser(UserData userData) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
