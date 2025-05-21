package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class GameMemoryDataAccess implements GameDAO {
    private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData createGame(GameData gameData) throws DataAccessException {
        return new GameData(0, "0", "0", "0", new ChessGame());
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
