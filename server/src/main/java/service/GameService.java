package service;

import dataaccess.AuthMemoryDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameMemoryDataAccess;
import request.CreateRequest;
import result.CreateResult;
import result.JoinResult;
import result.ListResult;
import result.LoginResult;

public class GameService {

    public CreateResult create(CreateRequest req) throws DataAccessException {
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        if (!authAccess.validateAuth(req.authToken())) {
            //
        }

        GameMemoryDataAccess gameAccess = new GameMemoryDataAccess();
        int gameID = gameAccess.createGame(req.gameName());
        return new CreateResult(gameID);
    }

    public JoinResult join() {
        return new JoinResult();
    }

    public ListResult list() {
        return new ListResult();
    }
}
