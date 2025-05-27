package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import request.RegisterRequest;

import java.util.HashMap;

public class UserMemoryDataAccess implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void createUser(RegisterRequest req) {
        String hashedPassword = BCrypt.hashpw(req.password(), BCrypt.gensalt());
        UserData user = new UserData(req.username(), hashedPassword, req.email());
        users.put(user.username(), user);
    }

    public void deleteAllUsers() {
        users.clear();
    }

    public UserData getUser(String username) {
        return users.get(username);
    }
}
