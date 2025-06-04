package service;

import exceptions.BadRequestException;
import exceptions.ParentException;
import exceptions.TakenException;
import exceptions.UnauthorizedException;
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
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public GameService(AuthDAO authAccess, GameDAO gameAccess) {
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    public CreateResult create(CreateRequest req) throws ParentException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        } else if (req.gameName() == null) {
            throw new BadRequestException("Error: bad request", 400);
        }

        int gameID = gameAccess.createGame(req.gameName());
        return new CreateResult(gameID);
    }

    public JoinResult join(JoinRequest req) throws ParentException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        GameData game = gameAccess.getGame(req.gameID());

        if (game == null || req.playerColor() == null) {
            throw new BadRequestException("Error: bad request", 400);
        }

        String username = authAccess.getUsername(req.authToken());

        if (req.playerColor().equals("white")) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                throw new TakenException("Error: already taken", 403);
            }

            game = gameAccess.updateGame(game, req.playerColor(), username);
            return new JoinResult(game.game());

        } else if (req.playerColor().equals("black")) {
            if (game.blackUsername() != null && !game.blackUsername().equals(username)) {
                throw new TakenException("Error: already taken", 403);
            }

            game = gameAccess.updateGame(game, req.playerColor(), username);
            return new JoinResult(game.game());

        } else {
            throw new BadRequestException("Error: bad request", 400);
        }
    }

    public ListResult list(ListRequest req) throws ParentException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        Collection<GameInfo> games = gameAccess.listGames();
        return new ListResult(games);
    }
}
