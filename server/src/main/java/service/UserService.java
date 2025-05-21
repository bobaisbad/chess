package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    private final UserDAO userAccess = new UserMemoryDataAccess();
    private final AuthDAO authAccess;

    public UserService(AuthDAO authAccess) {
        this.authAccess = authAccess;
    }

    public LoginResult login(LoginRequest req) throws DataAccessException, BadRequestException, UnauthorizedException {
        UserData user = userAccess.getUser(req.username());

        if (req.password() == null || req.username() == null) {
            throw new BadRequestException("Error: bad request", 400);
        } else if (user == null || !user.password().equals(req.password())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        AuthData auth = authAccess.createAuth(req.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException, UnauthorizedException {
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        authAccess.deleteAuth(req.authToken());
        return new LogoutResult();
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException, TakenException, BadRequestException {
        UserData user = userAccess.getUser(req.username());

        if (user != null) {
            throw new TakenException("Error: already taken", 403);
        } else if (req.username() == null || req.password() == null || req.email() == null) {
            throw new BadRequestException("Error: bad request", 400);
        }

        userAccess.createUser(req);
        AuthData auth = authAccess.createAuth(req.username());
        return new RegisterResult(auth.username(), auth.authToken());
    }

    public UserDAO getUserAccess() {
        return userAccess;
    }
}
