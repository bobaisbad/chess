package dataaccess;

import model.GameInfo;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import request.RegisterRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDatabaseAccess implements UserDAO {

    public void createUser(RegisterRequest req) {
        // var stmt = "INSERT INTO chess ("
        // UserData user = new UserData(req.username(), req.password(), req.email());
        // users.put(user.username(), user);
    }

    public void deleteAllUsers() {
        // users.clear();
    }

    public UserData getUser(String username) {
        return new UserData("sup", "pass", "gmail"); // users.get(username);
    }
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
