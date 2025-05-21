package dataaccess;

import model.AuthData;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    GameData createGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException; // (AuthData authToken);

    GameData updateGame(GameData gameData) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;
}
