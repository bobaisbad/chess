package service;

import dataaccess.AuthMemoryDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserMemoryDataAccess;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    // UUID.randomUUID().toString()

    public LoginResult login(LoginRequest req) throws DataAccessException {
        UserDAO userAccess = new UserMemoryDataAccess();
        UserData user = userAccess.getUser(req.username());

        if (user == null) {
            //
        } else if (!user.password().equals(req.password())) {
            //
        }

        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();
        AuthData auth = authAccess.createAuth(req.username());
        return new LoginResult(auth.username(), auth.authToken());
    }

    public LogoutResult logout(LogoutRequest req) throws DataAccessException {
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        if (!authAccess.validateAuth(req.authToken())) {
            //
        }

        authAccess.deleteAuth(req.authToken());
        return new LogoutResult();
    }

    public RegisterResult register(RegisterRequest req) throws DataAccessException {
        UserDAO userAccess = new UserMemoryDataAccess();
        UserData user = userAccess.getUser(req.username());

        if (user != null) {
            //
        }

        userAccess.createUser(req);
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();
        AuthData auth = authAccess.createAuth(req.username());
        return new RegisterResult(auth.username(), auth.authToken());
    }
}
