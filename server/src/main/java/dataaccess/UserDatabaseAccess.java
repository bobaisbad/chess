package dataaccess;

import Exceptions.DataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;
import java.sql.*;

public class UserDatabaseAccess implements UserDAO {

    public void createUser(RegisterRequest req) throws DataAccessException {
        var stmt = "INSERT INTO users (username, password, email) " +
                   "VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, req.username());
                prepStmt.setString(2, hashedPassword);
                prepStmt.setString(3, req.email());

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to insert new user", 500);
        }
    }

    public void deleteAllUsers() throws DataAccessException {
        DatabaseManager.deleteTable("users");
    }

    public UserData getUser(String name) throws DataAccessException {
        String stmt = "SELECT username, password, email " +
                      "FROM users " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, name);
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        String resultUsername = result.getString("username");
                        String password = result.getString("password");
                        String email = result.getString("email");

                        return new UserData(resultUsername, password, email);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve user", 500);
        }
    }
}
