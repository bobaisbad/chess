package dataaccess;

import model.UserData;
import request.RegisterRequest;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void createUser(RegisterRequest req) throws DataAccessException {
        UserData user = new UserData(req.username(), req.password(), req.email());
        users.put(user.username(), user);
    }

    public void deleteUser(UserData userData) throws DataAccessException {
        //
    }

    public void deleteAllUsers() throws DataAccessException {
        users.clear();
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}
