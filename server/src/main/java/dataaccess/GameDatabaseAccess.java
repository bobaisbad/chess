package dataaccess;

import Exceptions.DataAccessException;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameInfo;
import model.UserData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class GameDatabaseAccess implements GameDAO {

    public int createGame(String gameName) throws DataAccessException {
        var stmt = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) " +
                   "VALUES (?, ?, ?, ?)";
        var jsonGame = new Gson().toJson(new ChessGame());

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt, RETURN_GENERATED_KEYS)) {
                prepStmt.setString(1, null);
                prepStmt.setString(2, null);
                prepStmt.setString(3, gameName);
                prepStmt.setString(4, jsonGame);

                prepStmt.executeUpdate();

                ResultSet result = prepStmt.getGeneratedKeys();
                if (result.next()) {
                    return result.getInt(1);
                }

                return 0;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to insert new game", 500);
        }
    }

//    try (Connection conn = DatabaseManager.getConnection()) {
//
//        } catch (SQLException ex) {
//            throw new DataAccessException("Error: ", 500)
//        }

    public GameData getGame(int gameID) throws DataAccessException {
        String stmt = "SELECT gameID, whiteUsername, blackUsername, gameName, game " +
                      "FROM games " +
                      "WHERE gameID=?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setInt(1, gameID);
                try (ResultSet result = prepStmt.executeQuery()) {
                    result.next();
                    gameID = result.getInt("gameID");
                    String whiteUsername = result.getString("whiteUsername");
                    String blackUsername = result.getString("blackUsername");
                    String gameName = result.getString("gameName");
                    ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);

                    return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: failed to retrieve user", 500);
        }
    }

    public Collection<GameInfo> listGames() {
        HashSet<GameInfo> info = new HashSet<>();
        GameData data;

//        for (int key : games.keySet()) {
//            data = games.get(key);
//            info.add(new GameInfo(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName()));
//        }
        return info;
    }

    public void updateGame(GameData game, String playerColor, String username) {
        if (playerColor.equals("WHITE")) {
            // games.put(game.gameID(), new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game()));
        } else {
            // games.put(game.gameID(), new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game()));
        }
    }

    public void deleteAllGames() throws DataAccessException {
//        var stmt = "DROP TABLE IF EXISTS games";
//        try (Connection conn = DatabaseManager.getConnection()) {
//            try (var prepStmt = conn.prepareStatement(stmt)) {
//                prepStmt.executeUpdate();
//            }
//        } catch (SQLException ex) {
//            throw new DataAccessException("Error: unable to drop table", 500);
//        }
        DatabaseManager.deleteTable("games");
    }
}
