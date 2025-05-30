package service;

import exceptions.ParentException;
import dataaccess.*;

public class ClearService {
    private final AuthDAO authAccess;
    private final GameDAO gameAccess;
    private final UserDAO userAccess;

    public ClearService(AuthDAO authAccess, GameDAO gameAccess, UserDAO userAccess) {
        this.authAccess = authAccess;
        this.gameAccess = gameAccess;
        this.userAccess = userAccess;
    }

    public void clearAllData() throws ParentException {
        userAccess.deleteAllUsers();
        gameAccess.deleteAllGames();
        authAccess.deleteAllAuth();
    }
}
