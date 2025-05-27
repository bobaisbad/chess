package dataaccess;

import Exceptions.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDatabaseAccess implements AuthDAO {

    public AuthData createAuth(String username) {
        String token = UUID.randomUUID().toString();
        AuthData auth = new AuthData(token, username);
        // auths.put(token, auth);
        return auth;
    }

    public void deleteAuth(String authToken) {
        // auths.remove(authToken);
    }

    public void deleteAllAuth() throws DataAccessException {
//        var stmt = "DROP TABLE IF EXISTS auths";
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (var prepStmt = conn.prepareStatement(stmt)) {
//                prepStmt.executeUpdate();
//            }
//        } catch (SQLException ex) {
//            throw new DataAccessException("Error: unable to drop table", 500);
//        }
        DatabaseManager.deleteTable("auths");
    }

    public boolean validateAuth(String authToken) {
        return true; // !auths.containsKey(authToken);
    }

    public String getUsername(String authToken) {
        // AuthData data = auths.get(authToken);
        return "sup"; // data.username();
    }
}
