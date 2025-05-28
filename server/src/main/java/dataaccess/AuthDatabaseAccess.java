package dataaccess;

import Exceptions.DataAccessException;
import model.AuthData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthDatabaseAccess implements AuthDAO {

    public AuthData createAuth(String username) throws DataAccessException {
        String stmt = "INSERT INTO auths (authToken, username) " +
                      "VALUES (?, ?)";
        String token = UUID.randomUUID().toString();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, token);
                prepStmt.setString(2, username);

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to insert authToken", 500);
        }

        return new AuthData(token, username);
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String stmt = "DELETE FROM auths " +
                      "WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, authToken);

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to delete AuthData", 500);
        }
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

    public boolean validateAuth(String authToken) throws DataAccessException {
        System.out.println("Validating auth...");

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths " +
                      "WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, authToken);

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        return result.getInt("total") != 1;
                    }

                    return true;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve authToken", 500);
        }
    }

    public String getUsername(String authToken) throws DataAccessException {
        String stmt = "SELECT username " +
                      "FROM auths " +
                      "WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, authToken);

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        return result.getString("username");
                    }

                    return null;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve username", 500);
        }
    }
}
