package service;

import dataaccess.DataAccessException;
import dataaccess.GameMemoryDataAccess;
import dataaccess.UserMemoryDataAccess;
import dataaccess.AuthMemoryDataAccess;

public class ClearService {

    public void clearAllData() throws DataAccessException {
        UserMemoryDataAccess userAccess = new UserMemoryDataAccess();
        GameMemoryDataAccess gameAccess = new GameMemoryDataAccess();
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        userAccess.deleteAllUsers();
        gameAccess.deleteAllGames();
        authAccess.deleteAllAuth();
    }
}
