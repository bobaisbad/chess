package service;

import dataaccess.*;
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
//    private final AuthDAO authAccess = new AuthMemoryDataAccess();
    private final GameDAO gameAccess = new GameMemoryDataAccess();
    private final AuthDAO authAccess;

    public GameService(AuthDAO authAccess) {
        this.authAccess = authAccess;
    }

    public CreateResult create(CreateRequest req) throws DataAccessException, UnauthorizedException, BadRequestException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        } else if (req.gameName() == null) {
            throw new BadRequestException("Error: bad request", 400);
        }

        int gameID = gameAccess.createGame(req.gameName());
        return new CreateResult(gameID);
    }

    public JoinResult join(JoinRequest req) throws DataAccessException, UnauthorizedException, BadRequestException, TakenException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        GameData game = gameAccess.getGame(req.gameID());

        if (game == null || req.playerColor() == null) {
            throw new BadRequestException("Error: bad request", 400);
        }

        if (req.playerColor().equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new TakenException("Error: already taken", 403);
            }

            String username = authAccess.getUsername(req.authToken());
            gameAccess.updateGame(game, req.playerColor(), username);
            return new JoinResult();

        } else if (req.playerColor().equals("BLACK")) {
            if (game.blackUsername() != null) {
                throw new TakenException("Error: already taken", 403);
            }

            String username = authAccess.getUsername(req.authToken());
            gameAccess.updateGame(game, req.playerColor(), username);
            return new JoinResult();

        } else {
            throw new BadRequestException("Error: bad request", 400);
        }
    }

    public ListResult list(ListRequest req) throws DataAccessException, UnauthorizedException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        Collection<GameInfo> games = gameAccess.listGames();
        return new ListResult(games);
    }

    public GameDAO getGameAccess() {
        return gameAccess;
    }
}
