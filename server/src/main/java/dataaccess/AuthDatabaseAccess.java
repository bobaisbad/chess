package dataaccess;

import Exceptions.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDatabaseAccess implements AuthDAO {

    public AuthData createAuth(String username) throws DataAccessException {
        String stmt = "INSERT INTO auths (authToken, username) " +
                      "VALUES (?, ?)";
        String token = UUID.randomUUID().toString();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareCall(stmt)) {
                prepStmt.setString(1, token);
                prepStmt.setString(2, username);

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: ", 500);
        }

        return new AuthData(token, username);
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
