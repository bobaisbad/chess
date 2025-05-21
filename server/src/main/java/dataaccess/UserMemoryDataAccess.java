package dataaccess;

import model.UserData;
import request.RegisterRequest;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void createUser(RegisterRequest req) {
        UserData user = new UserData(req.username(), req.password(), req.email());
        users.put(user.username(), user);
    }

    public void deleteUser(UserData userData) {
        //
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
}
