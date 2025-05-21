package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData createUser(UserData userData);

    void deleteUser(UserData userData);

    UserData getUser(String username);
}
