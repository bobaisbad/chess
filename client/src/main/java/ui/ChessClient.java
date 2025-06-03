package ui;

import exceptions.ParentException;
import request.*;
import result.*;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private boolean loggedIn = false;
    private boolean gameStatus = false;

    public ChessClient() {
        this.server = new ServerFacade(8080);
        PreRepl pre = new PreRepl();
        pre.run(this);
    }

    public String preEval(String input) {
        Command cmd = getCommand(input);

        try {
            return switch (cmd.cmd()) {
                case "register" -> register(cmd.params());
                case "login" -> login(cmd.params());
                case "quit" -> quit();
                default -> help();
            };
        } catch (ParentException ex) {
            return ex.getMessage();
        }
    }

    public String gameEval(String input) {
        Command cmd = getCommand(input);
//
//        try {
//            return switch (cmd.cmd()) {
//                default -> help();
//            };
//        } catch (ParentException ignore) {
//            return ex.getMessage();
//        }

        return "";
    }

    public String postEval(String input) {
        Command cmd = getCommand(input);

        try {
            return switch (cmd.cmd()) {
                case "logout" -> logout(cmd.params());
                case "create" -> create(cmd.params());
                case "list" -> list(cmd.params());
                case "join" -> join(cmd.params());
                case "observe" -> observe(cmd.params());
                case "quit" -> quit();
                default -> help();
            };
        } catch (ParentException ex) {
            return ex.getMessage();
        }
    }

    private Command getCommand(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String cmd = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = (tokens.length > 1) ? Arrays.copyOfRange(tokens, 1, tokens.length) : new String[] {};
        return new Command(cmd, params);
    }

    private String register(String[] params) throws ParentException {
        if (params.length == 3) {
            RegisterRequest req = new RegisterRequest(params[0], params[1], params[2]);
            RegisterResult res = server.register(req);
            authToken = res.authToken();
            username = res.username();
            loggedIn = true;
            return "Logged in as " + username;
        }
        throw new ParentException("Expected: <username> <password> <email>", 400);
    }

    private String login(String[] params) throws ParentException {
        if (params.length == 2) {
            LoginRequest req = new LoginRequest(params[0], params[1]);
            LoginResult res = server.login(req);
            authToken = res.authToken();
            username = res.username();
            loggedIn = true;
            System.out.println(authToken);
            return "Logged in as " + username;
        }
        throw new ParentException("Expected: <username> <password>", 400);
    }

    private String logout(String[] params) throws ParentException {
        if (params.length == 0) {
            LogoutRequest req = new LogoutRequest(authToken);
            server.logout(req);
            loggedIn = false;
            return "Logged out";
        }
        throw new ParentException("Expected nothing", 400);
    }

    private String create(String[] params) throws ParentException {
        if (params.length == 1) {
            CreateRequest req = new CreateRequest(params[0], authToken);
            CreateResult res = server.create(req);
            return "Created the chess game " + params[0] + " with a gameID of " + res.gameID();
        }
        throw new ParentException("Expected: <gameName>", 400);
    }

    private String list(String[] params) throws ParentException {
        if (params.length == 0) {
            ListRequest req = new ListRequest(authToken);
            ListResult res = server.list(req);
            return "Games: " + res.games().toString();
        }
        throw new ParentException("Expected nothing", 400);
    }

    private String join(String[] params) throws ParentException {
        if (params.length == 2) {
            JoinRequest req = new JoinRequest(params[1], Integer.parseInt(params[0]), username);
            server.join(req);
            gameStatus = true;
            return "Joined game " + params[0] + " as " + params[1];
        }
        throw new ParentException("Expected: <gameID> <white | black>", 400);
    }

    private String observe(String[] params) throws ParentException {
        return "";
    }

    private String help() {
        if (!loggedIn) {
            return """
                   register <USERNAME> <PASSWORD> <EMAIL> - register to create an account
                   login <USERNAME> <PASSWORD> - login to play chess
                   quit - leave the program
                   help - print out possible commands
                   """;
        } else {
            return """
                   create <GAMENAME> - create a new game
                   list - list all games and who's playing
                   join <GAMEID> <WHITE | BLACK> - join and play a chess game
                   observer <GAMEID> - observe a game in progress
                   logout - logout of your account
                   quit - leave the program
                   help - print out possible commands
                   """;
        }
    }

    private String quit() {
        loggedIn = false;
        gameStatus = false;
        return  "quit";
    }

    public boolean getLoginStatus() {
        return loggedIn;
    }

    public boolean getGameStatus() {
        return gameStatus;
    }
}