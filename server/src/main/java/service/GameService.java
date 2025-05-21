package service;

import dataaccess.AuthMemoryDataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameMemoryDataAccess;
import model.GameData;
import model.GameInfo;
import request.CreateRequest;
import request.JoinRequest;
import request.ListRequest;
import result.CreateResult;
import result.JoinResult;
import result.ListResult;

import java.util.Collection;

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

    public JoinResult join(JoinRequest req) throws DataAccessException {
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        if (!authAccess.validateAuth(req.authToken())) {
            //
        }

        GameMemoryDataAccess gameAccess = new GameMemoryDataAccess();
        GameData game = gameAccess.getGame(req.gameID());

        if (game == null) {
            //
        } else {
            if (req.playerColor().equals("WHITE")) {
                if (game.whiteUsername() != null) {
                    //
                }

                String username = authAccess.getUsername(req.authToken());
                gameAccess.updateGame(game, req.playerColor(), username);
                return new JoinResult();

            } else if (req.playerColor().equals("BLACK")) {
                if (game.blackUsername() != null) {
                    //
                }

                String username = authAccess.getUsername(req.authToken());
                gameAccess.updateGame(game, req.playerColor(), username);
                return new JoinResult();

            } else {
                //
            }
        }

        return new JoinResult();
    }

    public ListResult list(ListRequest req) throws DataAccessException {
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        if (!authAccess.validateAuth(req.authToken())) {
            //
        }

        GameMemoryDataAccess gameAccess = new GameMemoryDataAccess();
        Collection<GameInfo> games = gameAccess.listGames();
        return new ListResult(games);
    }
}
