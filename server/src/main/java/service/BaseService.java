package service;

import dataaccess.AuthMemoryDataAccess;
import dataaccess.DataAccessException;

public class BaseService {

    public void validate(String authToken) throws DataAccessException {
        AuthMemoryDataAccess authAccess = new AuthMemoryDataAccess();

        if (authAccess.validateAuth(authToken)) {
            //
        }
    }
}
