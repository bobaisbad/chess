package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    boolean validateAuth(String authToken) throws DataAccessException;

    String getUsername(String authToken) throws DataAccessException;

    void deleteAllAuth() throws DataAccessException;
}
