package dataaccess;

import model.AuthData;

import java.util.UUID;

public class AuthDatabaseAccess implements AuthDAO {
    DatabaseManager manager;

    public AuthDatabaseAccess(DatabaseManager manager) {
        this.manager = manager;
    }

    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        // auths.put(token, auth);
        return auth;
    }

    public void deleteAuth(String authToken) {
        // auths.remove(authToken);
    }

    public void deleteAllAuth() {
        // auths.clear();
    }

    public boolean validateAuth(String authToken) {
        return true; // !auths.containsKey(authToken);
    }

    public String getUsername(String authToken) {
        // AuthData data = auths.get(authToken);
        return "sup"; // data.username();
    }
}
