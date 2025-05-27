package dataaccess;

import Exceptions.DataAccessException;
import chess.ChessGame;
import model.GameData;
import model.GameInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;

public class GameDatabaseAccess implements GameDAO {

    private int gameCount = 0;

    public int createGame(String gameName) {
        gameCount++;
        ChessGame game = new ChessGame();
        GameData data = new GameData(gameCount, null, null, gameName, game);
        // games.put(gameCount, data);
        return gameCount;
    }

    public GameData getGame(int gameID) {
        return new GameData(1, "sup", "sup", "sup", null); // games.get(gameID);
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
