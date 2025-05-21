package service;

import result.CreateResult;
import result.JoinResult;
import result.ListResult;
import result.LoginResult;

public class GameService {

    public CreateResult create() {
        return new CreateResult();
    }

    public JoinResult join() {
        return new JoinResult();
    }

    public ListResult list() {
        return new ListResult();
    }
}
