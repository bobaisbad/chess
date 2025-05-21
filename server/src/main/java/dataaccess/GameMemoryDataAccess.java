package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameMemoryDataAccess implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int gameCount = 0;

    public int createGame(String gameName) throws DataAccessException {
        gameCount++;
        ChessGame game = new ChessGame();
        GameData data = new GameData(gameCount, "", "", gameName, game);
        games.put(gameCount, data);
        return gameCount;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return new GameData(0, "0", "0", "0", new ChessGame());
    }

    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>();
    } // (AuthData authToken);

    public GameData updateGame(GameData gameData) throws DataAccessException {
        return new GameData(0, "0", "0", "0", new ChessGame());
    }

    public void deleteGame(int gameID) throws DataAccessException {
        //
    }

    public void deleteAllGames() throws DataAccessException {
        games.clear();
    }
}
