package websocket;

import chess.*;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exceptions.DataAccessException;
import exceptions.ParentException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userAccess;
    private final GameDAO gameAccess;
    private final AuthDAO authAccess;

    public WebSocketHandler(UserDAO userAccess, GameDAO gameAccess, AuthDAO authAccess) {
        this.userAccess = userAccess;
        this.gameAccess = gameAccess;
        this.authAccess = authAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, ParentException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case CONNECT -> connect(cmd.getGameID(), cmd.getAuthToken(), cmd.getColor(), session);
            case MAKE_MOVE -> move(cmd.getAuthToken(), cmd.getGameID(), cmd.getMove(), cmd.getColor(), session);
            case LEAVE -> leave(cmd.getGameID(), cmd.getColor(), cmd.getAuthToken(), session);
            case RESIGN -> resign(cmd.getGameID(), cmd.getAuthToken(), cmd.getColor(), session);
        }
    }

    private void connect(int gameID, String authToken, String color, Session session) throws ParentException, IOException {
        String username = authAccess.getUsername(authToken);
        connections.add(authToken, session, gameID, username);

        if (username == null) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, null, serverMsg);
            connections.remove(authToken);
            return;
        }

        GameData data = gameAccess.getGame(gameID);

        if (data == null) {
            String error = "Error: invalid gameID";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            connections.remove(authToken);
            return;
        }

        ChessGame game = data.game();

        String notification = username + " just joined the game as " + ((color != null) ? color : "an observer");

        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null);
        serverMsg.setGame(game);
        connections.send(authToken, session, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
    }

    private void move(String authToken, int gameID, ChessMove move, String color, Session session) throws ParentException, IOException {
        String username = authAccess.getUsername(authToken);

        if (authAccess.validateAuth(authToken)) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        GameData data = gameAccess.getGame(gameID);
        ChessGame game = data.game();

        if (game == null || game.getGameOver()) {
            String error = "Error: the game is over";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        ChessPosition start = move.getStartPosition();
        ChessPiece piece = game.getBoard().getPiece(start);
        String pieceColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? "white" : "black";

        if (username.equals(data.whiteUsername())) {
            color = "white";
        } else if (username.equals(data.blackUsername())) {
            color = "black";
        }

        if (!pieceColor.equals(color)) {
            String error = "Error: invalid move";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

//        System.out.println("Start Row: " + start.getRow());
//        System.out.println("Start Col: " + start.getColumn());
//        System.out.println("End Row: " + move.getEndPosition().getRow());
//        System.out.println("End Col: " + move.getEndPosition().getColumn());
//        System.out.println("Piece: " + piece);
//        System.out.println("Player color: " + color);
//        System.out.println("Turn: " + game.getTeamTurn());

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            String error = "Error: invalid move";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        gameAccess.updateGameState(gameID, game);
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null);
        serverMsg.setGame(game);
        connections.broadcast(gameID, "", serverMsg);

        String notification = username + " moved " + move.getStartPosition() + " to " + move.getEndPosition();
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);

        String enemy = (color.equals("white")) ? data.blackUsername() : data.whiteUsername();

        ChessGame.TeamColor team = (color.equals("white")) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
        boolean check = game.isInCheck(team);
        boolean mate = game.isInCheckmate(team);
        boolean stale = game.isInStalemate(team);

        if (check && !mate && !stale) { // check
            notification = enemy + " is in check!";
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
            connections.broadcast(gameID, "", serverMsg);
        } else if (check && mate && !stale) { // checkmate
            notification = enemy + " is in checkmate!";
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
//            serverMsg.setGameOver(true);
            serverMsg.setWinner(color);
            connections.broadcast(gameID, "", serverMsg);
        } else if (!check && mate && stale) { // stalemate
            notification = "Stalemate!";
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
//            serverMsg.setGameOver(true);
            connections.broadcast(gameID, "", serverMsg);
        }
    }

    private void leave(int gameID, String color, String authToken, Session session) throws ParentException, IOException {
        String username = authAccess.getUsername(authToken);

        if (authAccess.validateAuth(authToken)) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        GameData data = gameAccess.getGame(gameID);

        if (username.equals(data.whiteUsername())) {
            gameAccess.updateGame(data, "white", null);
        } else if (username.equals(data.blackUsername())) {
            gameAccess.updateGame(data, "black", null);
        }

        String notification = username + " as " + color + " left the game";
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
        connections.remove(authToken);
    }

    private void resign(int gameID, String authToken, String color, Session session) throws ParentException, IOException {
        String username = authAccess.getUsername(authToken);

//        gameAccess.updateGameState(gameID, game);
//        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null);
//        serverMsg.setGame(game);
//        connections.broadcast(gameID, authToken, serverMsg);
        if (authAccess.validateAuth(authToken)) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        GameData data = gameAccess.getGame(gameID);

        if (!username.equals(data.blackUsername()) && !username.equals(data.whiteUsername())) {
            String error = "Error: an observer can't resign the game";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        } else if (data.game().getGameOver()) {
            String error = "Error: the game is over";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, session, serverMsg);
            return;
        }

        data.game().setGameOver(true);
        gameAccess.updateGameState(gameID, data.game());

        String notification = username + " as " + color + " resigned the game";
        String msg = "You resigned the game as " + color;

        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg, null);
        connections.send(authToken, session, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
    }
}
