package ui;

import chess.ChessGame;
import exceptions.ParentException;
import model.GameInfo;
import request.*;
import result.*;
import server.ServerFacade;
import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    private String username = null;
    private String authToken = null;
    private final ServerFacade server;
    private boolean loggedIn = false;
    private boolean gameStatus = false;
    private String userColor = null;
    private ChessGame game = null;
    private boolean quit = false;
    private boolean resigned = false;
    private HashMap<String, Integer> listedGames = new HashMap<>();

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

        try {
            return switch (cmd.cmd()) {
                case "quit" -> quit();
                case "leave" -> leave();
                case "redraw" -> "";
//                case "move" -> move(cmd.params());
                case "resign" -> resign(cmd.params());
//                case "highlight" -> highlight(cmd.params());
                default -> help();
            };
        } catch (ParentException ex) {
            return ex.getMessage();
        }
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

    public String resignedEval(String input) {
        Command cmd = getCommand(input);

        try {
            return switch (cmd.cmd()) {
                case "quit" -> quit();
                case "leave" -> leave();
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
        try {
            if (params.length == 3) {
                RegisterRequest req = new RegisterRequest(params[0], params[1], params[2]);
                RegisterResult res = server.register(req);
                authToken = res.authToken();
                username = res.username();
                loggedIn = true;
                list(new String[] {});
                return "Logged in as " + username;
            }
            throw new ParentException("Expected: <username> <password> <email>", 400);
        } catch (ParentException ex) {
            throw new ParentException("Error: username already taken", 400);
        }
    }

    private String login(String[] params) throws ParentException {
        try {
            if (params.length == 2) {
                LoginRequest req = new LoginRequest(params[0], params[1]);
                LoginResult res = server.login(req);
                authToken = res.authToken();
                username = res.username();
                loggedIn = true;
                list(new String[] {});
                return "Logged in as " + username;
            }
            throw new ParentException("Expected: <username> <password>", 400);
        } catch (ParentException ex) {
            throw new ParentException("Error: incorrect username or password", 400);
        }
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
            return "Created the chess game " + params[0];
        }
        throw new ParentException("Expected: <gameName>", 400);
    }

    private String list(String[] params) throws ParentException {
        if (params.length == 0) {
            ListRequest req = new ListRequest(authToken);
            ListResult res = server.list(req);
            StringBuilder list = new StringBuilder();
            GameInfo[] games = res.games().toArray(new GameInfo[0]);
            listedGames.clear();

            for (int i = 0; i < games.length; i++) {
                list.append("\n")
                            .append(i + 1)
                            .append(". Game Name: ")
                            .append(games[i].gameName())
                            .append(", White: ")
                            .append(games[i].whiteUsername())
                            .append(", Black: ")
                            .append(games[i].blackUsername());
                listedGames.put("" + (i + 1), games[i].gameID());
            }
            return "Games:" + list;
        }
        throw new ParentException("Expected nothing", 400);
    }

    private String join(String[] params) throws ParentException {
        try {
            if (params.length == 2) {
                int gameID = (listedGames.get(params[0]) == null) ? 0: listedGames.get(params[0]);
                JoinRequest req = new JoinRequest(params[1], gameID, authToken);
                JoinResult res = server.join(req);
                gameStatus = true;
                resigned = false;
                userColor = params[1];
                game = res.game();
                return "Joined game " + params[0] + " as " + params[1];
            }
            throw new ParentException("Expected: <game#> <white | black>", 400);
        } catch (ParentException ex) {
            throw new ParentException(ex.getMessage(), 400);
        } catch (NumberFormatException ex) {
            throw new ParentException("Expected: <game#> <white | black>", 400);
        }
    }

    private String observe(String[] params) throws ParentException {
        if (params.length == 1) {
//            int gameID = (listedGames.get(params[0]) == null) ? 0: listedGames.get(params[0]);
            gameStatus = true;
            resigned = true;
            return "Observing game " + params[0];
        }
        throw new ParentException("Expected <game#>", 400);
    }

    private String help() {
        if (!loggedIn) {
            return """
                   register <USERNAME> <PASSWORD> <EMAIL> - register to create an account
                   login <USERNAME> <PASSWORD> - login to play chess
                   quit - leave the program
                   help - print out possible commands""";
        } else if (!gameStatus) {
            return """
                   create <GAMENAME> - create a new game
                   list - list all games and who's playing
                   join <GAME#> <WHITE | BLACK> - join and play a chess game
                   observe <GAME#> - observe a game in progress
                   logout - logout of your account
                   quit - leave the program
                   help - print out possible commands""";
        } else if (resigned) {
                return """
                   leave - leave the current game
                   quit - leave the program
                   help - print out possible commands""";
        } else {
//            return """
//                   redraw chess board - redraws the chess board
//                   leave - leave the current game
//                   make move <PIECE> <MOVE> - select and move a piece
//                   resign - forfeit and end the game
//                   highlight legal moves <PIECE> - highlights where the selected piece can move
//                   quit - leave the program
//                   help - print out possible commands""";
            return """
                   redraw - redraws the chess board
                   leave - leave the current game
                   move <PIECE> <MOVE> - select and move a piece
                   resign - forfeit and end the game
                   highlight <PIECE> - highlights where the selected piece can move
                   quit - leave the program
                   help - print out possible commands""";
        }
    }

    private String quit() throws ParentException {
        if (loggedIn) {
            logout(new String[] {});
        }
        loggedIn = false;
        gameStatus = false;
        resigned = false;
        quit = true;
        return "quit";
    }

    private String leave() {
        gameStatus = false;
        resigned = false;
        return "Leaving the game...";
    }

    private String resign(String[] params) {
        if (params.length == 0) {
            return "resign";
        } else if (params[0].equals("y")) {
            resigned = true;

        }
    }

    public boolean getLoginStatus() {
        return loggedIn;
    }

    public boolean getGameStatus() {
        return gameStatus;
    }

    public String getColor() {
        return userColor;
    }

    public ChessGame getGame() {
        return game;
    }

    public boolean getQuit() {
        return quit;
    }

    public boolean getResigned() {
        return resigned;
    }
}