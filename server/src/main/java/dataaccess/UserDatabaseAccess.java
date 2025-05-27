package dataaccess;

import Exceptions.DataAccessException;
import model.GameInfo;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseAccess implements UserDAO {

    public void createUser(RegisterRequest req) throws DataAccessException {
        var stmt = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, req.username());
                prepStmt.setString(2, hashedPassword);
                prepStmt.setString(3, req.email());

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to insert new user", 500);
        }
    }

    public void deleteAllUsers() throws DataAccessException {
        DatabaseManager.deleteTable("users");
    }

    public UserData getUser(String username) throws DataAccessException {
        String stmt = "SELECT username, password, email FROM users WHERE username=?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, username);
                try (ResultSet result = prepStmt.executeQuery()) {
                    result.next();
                    username = result.getString(1);
                    String password = result.getString(2);
                    String email = result.getString(3);

                    return new UserData(username, password, email);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to retrieve user", 500);
        }
    }

//    try (Connection conn = DatabaseManager.getConnection()) {
//
//    } catch (SQLException ex) {
//        throw new DataAccessException("Error: ", 500)
//    }

}

//    public List<GameInfo> test() {
//        String connectionURL = "jdbc:mysql://localhost:8080/Name?" + "user=boba&password=password";
//        Connection connection = null;
//
//        String connectionURL2 = "jdbc:mysql://localhost:8080/Name?";
//        CREATE USER 'boba'@'localhost' IDENTIFIED BY 'password';
//        GRANT ALL on Name.* to 'boba'@'localhost';
//
//        try (Connection c = DriverManager.getConnection(connectionURL)) {
//            connection = c;
//
//            // transactions
//            connection.setAutoCommit(false);
//
//            connection.commit();
//        } catch (SQLException ex) {
//            if (connection != null && !connection.isClosed()) {
//                connection.rollback();
//            }
//
//            throw ex;
//        }
//
//        List<GameInfo> games = new ArrayList<>();
//        String sql = "select id, whiteUsername, blackUsername, gameName from games";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql);
//             ResultSet rs = stmt.executeQuery()) {
//
//            while (rs.next()) {
//                int gameID = rs.getInt(1);
//                String whiteUsername = rs.getString(2);
//                String blackUsername = rs.getString(3);
//                String gameName = rs.getString(4);
//                games.add(new GameInfo(gameID, whiteUsername, blackUsername, gameName));
//            }
//        } catch (SQLException ex) {
//            // error
//        }
//
//        String sql = "update game " +
//                     "set whiteUsername = ?, blackUsername = ?, gameName = ? " +
//                     "where id = ?";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setString(1, game.whiteUsername);
//            stmt.setString(2, game.blackUsername);
//            stmt.setString(3, game.gameName);
//
//            if (stmt.executeUpdate() == 1) {
//                // print success
//            } else {
//                // print failure
//            }
//        } catch (SQLException ex) {
//            // error
//        }
//
//        PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//        try (ResultSet generateKeys = stmt.getGeneratedKeys()) {
//            generateKeys.next();
//            int id = generateKeys.getInt(1);
//            game.setID = id;
//        }
//
//        sql = "ALTER TABLE book AUTO_INCREMENT = 1";
//
//        return games;
//    }

//    public List<GameInfo> list() {
//        String sql = "select id, whiteUsername, blackUsername, gameName from games";
//
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//
//        } catch (SQLException ex) {
//            // error
//        }
//    }
// }
