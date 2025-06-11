package dataaccess;

import chess.ChessGame;
import exceptions.DataAccessException;
import model.GameData;
import model.GameInfo;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class GameMemoryDataAccess implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int gameCount = 0;

    public int createGame(String gameName) {
        gameCount++;
        ChessGame game = new ChessGame();
        GameData data = new GameData(gameCount, null, null, gameName, game);
        games.put(gameCount, data);
        return gameCount;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public Collection<GameInfo> listGames() {
        HashSet<GameInfo> info = new HashSet<>();
        GameData data;

        for (int key : games.keySet()) {
            data = games.get(key);
            info.add(new GameInfo(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName()));
        }
        return info;
    }

    public GameData updateGame(GameData game, String playerColor, String username) {
        GameData updatedGame;
        if (playerColor.equals("white")) {
            updatedGame = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            games.put(game.gameID(), updatedGame);
        } else {
            updatedGame = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
            games.put(game.gameID(), updatedGame);
        }
        return updatedGame;
    }

    public void deleteAllGames() {
        games.clear();
    }

    public void updateGameState(int gameID, ChessGame game) throws DataAccessException {}
}
