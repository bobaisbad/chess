package dataaccess;

import model.UserData;

public interface UserDAO {

    UserData createUser(UserData userData);

    UserData getUser(String username);
}
