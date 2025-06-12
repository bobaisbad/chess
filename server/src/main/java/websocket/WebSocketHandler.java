package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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
            case MAKE_MOVE -> move(cmd.getAuthToken(), cmd.getGameID(), cmd.getMove(), cmd.getColor());
            case LEAVE -> leave(cmd.getGameID(), cmd.getColor(), cmd.getAuthToken());
            case RESIGN -> resign(cmd.getGameID(), cmd.getAuthToken(), cmd.getGame(), cmd.getColor());
        }
    }

    private void connect(int gameID, String authToken, String color, Session session) throws DataAccessException, IOException {
        connections.add(authToken, session, gameID);
        String username = authAccess.getUsername(authToken);

        if (username == null) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, serverMsg);
            connections.remove(authToken);
            return;
        }

        GameData data = gameAccess.getGame(gameID);

        if (data == null) {
            String error = "Error: invalid gameID";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, serverMsg);
            connections.remove(authToken);
            return;
        }

        ChessGame game = data.game();

        String notification = username + " just joined the game as " + ((color != null) ? color : "an observer");

        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null);
        serverMsg.setGame(game);
        connections.send(authToken, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
    }

    private void move(String authToken, int gameID, ChessMove move, String color) throws ParentException, IOException {
        String username = authAccess.getUsername(authToken);

        if (username == null) {
            String error = "Error: invalid authToken";
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, null, error);
            connections.send(authToken, serverMsg);
            return;
        }

        GameData data = gameAccess.getGame(gameID);
        ChessGame game = data.game();

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new ParentException("Error: invalid move", 400);
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
            serverMsg.setGameOver(true);
            serverMsg.setWinner(color);
            connections.broadcast(gameID, "", serverMsg);
        } else if (!check && mate && stale) { // stalemate
            notification = "Stalemate!";
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
            serverMsg.setGameOver(true);
            connections.broadcast(gameID, "", serverMsg);
        }
    }

    private void leave(int gameID, String color, String authToken) throws DataAccessException, IOException {
        GameData data = gameAccess.getGame(gameID);
        gameAccess.updateGame(data, color, null);

        String username = (color.equals("white")) ? data.whiteUsername() : data.blackUsername();
        String notification = username + " as " + color + " left the game";
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
        connections.remove(authToken);
    }

    private void resign(int gameID, String authToken, ChessGame game, String color) throws DataAccessException, IOException {
        gameAccess.updateGameState(gameID, game);
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, null, null);
        serverMsg.setGame(game);
        connections.broadcast(gameID, "", serverMsg);

        String username = authAccess.getUsername(authToken);
        String notification = username + " as " + color + " resigned the game";
        String msg = "You resigned the game as " + color;

        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg, null);
        connections.send(authToken, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification, null);
        connections.broadcast(gameID, authToken, serverMsg);
    }
}
