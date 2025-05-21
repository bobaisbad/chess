package service;

import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

public class UserService {
    // UUID.randomUUID().toString()

    public LoginResult login() {
        return new LoginResult();
    }

    public LogoutResult logout() {
        return new LogoutResult();
    }

    public RegisterResult register() {
        return new RegisterResult();
    }
}
