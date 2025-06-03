package ui;

import exceptions.ParentException;
import request.RegisterRequest;
import result.RegisterResult;
import server.ServerFacade;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private final PreRepl pre = new PreRepl();

    public ChessClient() {
        this.server = new ServerFacade(8080);
    }

    public String command(String input) {
        //
    }

    private clear() {
        //
    }

    private String register(String... params) throws ParentException {
        if (params.length == 3) {
            RegisterRequest req = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult result = server.register(req);
            authToken = result.authToken();
            username = result.username();
            return "Logged in as " + username;
        }
        throw new ParentException("Expected: <username> <password> <email>", 400);
    }

    private String login(String... params) {

    }

    private logout() {
        //
    }

    private create() {
        //
    }

    private list() {
        //
    }

    private join() {
        //
    }
}