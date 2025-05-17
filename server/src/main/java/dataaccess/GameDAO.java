package dataaccess;

import model.AuthData;
import model.GameData;

public interface GameDAO {

    GameData createGame(GameData gameData);

    GameData getGame(int gameID);

    GameData listGames(AuthData authToken);

    GameData updateGame(GameData gameData);
}
