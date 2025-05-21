package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthMemoryDataAccess implements AuthDAO {
    private HashMap<String, AuthData> auths = new HashMap<>();

    public AuthData createAuth(String username) throws DataAccessException {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        auths.put(token, auth);
        return auth;
        // add said data to database
        // UUID.randomUUID().toString()
    }

//    public AuthData getAuth(String username) throws DataAccessException {
//        return auths.get(username);
//    }

    public void deleteAuth(String authToken) throws DataAccessException {
        auths.remove(authToken);
    }

    public boolean validateAuth(String authToken) throws DataAccessException {
        return auths.containsKey(authToken);
    }
}
