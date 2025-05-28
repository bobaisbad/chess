package service;

import Exceptions.BadRequestException;
import Exceptions.ParentException;
import Exceptions.TakenException;
import Exceptions.UnauthorizedException;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    // private final UserDAO userAccess = new UserMemoryDataAccess();
    private final UserDAO userAccess;
    private final AuthDAO authAccess;

    public UserService(AuthDAO authAccess, UserDAO userAccess) {
        this.userAccess = userAccess;
        this.authAccess = authAccess;
    }

    public LoginResult login(LoginRequest req) throws ParentException {
        System.out.println("Logging in...");
        UserData user = userAccess.getUser(req.username());

        if (req.password() == null || req.username() == null) {
            throw new BadRequestException("Error: bad request", 400);
        } else if (user == null || !BCrypt.checkpw(req.password(), user.password())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        AuthData auth = authAccess.createAuth(req.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public LogoutResult logout(LogoutRequest req) throws ParentException {
        System.out.println("Logout...");
        if (authAccess.validateAuth(req.authToken())) {
            throw new UnauthorizedException("Error: unauthorized", 401);
        }

        authAccess.deleteAuth(req.authToken());
        return new LogoutResult();
    }

    public RegisterResult register(RegisterRequest req) throws ParentException {
        System.out.println("Registering...");
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
