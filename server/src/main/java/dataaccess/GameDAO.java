package dataaccess;

import exceptions.DataAccessException;
import model.GameData;
import model.GameInfo;
import java.util.Collection;

public interface GameDAO {

    int createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    Collection<GameInfo> listGames() throws DataAccessException;

    GameData updateGame(GameData game, String playerColor, String username) throws DataAccessException;

    void deleteAllGames() throws DataAccessException;
}
