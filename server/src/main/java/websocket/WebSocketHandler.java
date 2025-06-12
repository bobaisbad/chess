package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import exceptions.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

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
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case CONNECT -> connect(cmd.getGameID(), cmd.getAuthToken(), cmd.getColor(), session);
            case MAKE_MOVE -> move(cmd.getAuthToken(), cmd.getGame(), cmd.getGameID(), cmd.getMove());
            case LEAVE -> leave(cmd.getGameID(), cmd.getColor(), cmd.getAuthToken());
            case RESIGN -> resign(cmd.getGameID(), cmd.getAuthToken(), cmd.getGame(), cmd.getColor());
        }
    }

    private void connect(int gameID, String authToken, String color, Session session) throws DataAccessException, IOException {
        connections.add(authToken, session, gameID);
        String username = authAccess.getUsername(authToken);

        String msg = "You just joined the game as " + ((color != null) ? color : "an observer");
        String notification = username + " just joined the game as " + ((color != null) ? color : "an observer");

        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, msg);
        connections.send(authToken, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(gameID, authToken, serverMsg);
    }

    private void move(String authToken, ChessGame game, int gameID, MoveCommand move) throws DataAccessException, IOException {
        if (!authAccess.validateAuth(authToken)) {
            gameAccess.updateGameState(gameID, game);
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "");
            serverMsg.setGame(game);
            connections.broadcast(gameID, "", serverMsg);

            String username = authAccess.getUsername(authToken);
            String notification = username + " moved " + move.start() + " to " + move.end();
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
            connections.broadcast(gameID, authToken, serverMsg);

            GameData data = gameAccess.getGame(gameID);
            String enemy = (move.color().equals("white")) ? data.blackUsername() : data.whiteUsername();

            if (move.check() && !move.mate() && !move.stale()) { // check
                notification = enemy + " is in check!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                connections.broadcast(gameID, "", serverMsg);
            } else if (move.check() && move.mate() && !move.stale()) { // checkmate
                notification = enemy + " is in checkmate!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                serverMsg.setGameOver(true);
                serverMsg.setWinner(move.color());
                connections.broadcast(gameID, "", serverMsg);
            } else if (!move.check() && move.mate() && move.stale()) { // stalemate
                notification = "Stalemate!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                serverMsg.setGameOver(true);
                connections.broadcast(gameID, "", serverMsg);
            }
        }
    }

    private void leave(int gameID, String color, String authToken) throws DataAccessException, IOException {
        GameData data = gameAccess.getGame(gameID);
        gameAccess.updateGame(data, color, null);

        String username = (color.equals("white")) ? data.whiteUsername() : data.blackUsername();
        String notification = username + " as " + color + " left the game";
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(gameID, authToken, serverMsg);
        connections.remove(authToken);
    }

    private void resign(int gameID, String authToken, ChessGame game, String color) throws DataAccessException, IOException {
        gameAccess.updateGameState(gameID, game);
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "");
        serverMsg.setGame(game);
        connections.broadcast(gameID, "", serverMsg);

        String username = authAccess.getUsername(authToken);
        String notification = username + " as " + color + " resigned the game";
        String msg = "You resigned the game as " + color;

        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, msg);
        connections.send(authToken, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(gameID, authToken, serverMsg);
    }
}
