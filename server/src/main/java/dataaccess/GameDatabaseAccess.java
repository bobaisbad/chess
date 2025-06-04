package dataaccess;

import exceptions.DataAccessException;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.GameInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

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
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to insert new game", 500);
        }
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String stmt = "SELECT gameID, whiteUsername, blackUsername, gameName, game " +
                      "FROM games " +
                      "WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setInt(1, gameID);
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        gameID = result.getInt("gameID");
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");
                        String gameName = result.getString("gameName");
                        ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);

                        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
                    }

                    return null;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve game", 500);
        }
    }

    public Collection<GameInfo> listGames() throws DataAccessException {
        String stmt = "SELECT gameID, whiteUsername, blackUsername, gameName " +
                      "FROM games";
        HashSet<GameInfo> info = new HashSet<>();

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    while (result.next()) {
                        int gameID = result.getInt("gameID");
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");
                        String gameName = result.getString("gameName");
                        info.add(new GameInfo(gameID, whiteUsername, blackUsername, gameName));
                    }

                    return info;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve all games", 500);
        }
    }

    public GameData updateGame(GameData game, String playerColor, String username) throws DataAccessException {
        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (playerColor.equals("WHITE")) {
            whiteUsername = username;
        } else {
            blackUsername = username;
        }

         String stmt = "UPDATE games " +
                       "SET whiteUsername = ?, blackUsername = ? " +
                       "WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, whiteUsername);
                prepStmt.setString(2, blackUsername);
                prepStmt.setInt(3, game.gameID());

                prepStmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to update game", 500);
        }

        return new GameData(game.gameID(), whiteUsername, blackUsername, game.gameName(), game.game());
    }

    public void deleteAllGames() throws DataAccessException {
        DatabaseManager.deleteTable("games");
    }
}
