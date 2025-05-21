package dataaccess;

import model.AuthData;

public interface AuthDAO {

    AuthData createAuth(String username) throws DataAccessException;

    // AuthData getAuth(String username);

    AuthData deleteAuth(AuthData authToken) throws DataAccessException;

    boolean validateAuth(String authToken) throws DataAccessException;
}
