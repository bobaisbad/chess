package dataaccess;

import exceptions.DataAccessException;
import model.UserData;
import request.RegisterRequest;

public interface UserDAO {

    void createUser(RegisterRequest req) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteAllUsers() throws DataAccessException;
}
