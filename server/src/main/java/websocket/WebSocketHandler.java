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
            case CONNECT -> connect(cmd.getAuthToken(), cmd.getColor(), session);
//            case MAKE_MOVE -> move(cmd.getAuthToken(), cmd.getMove(), cmd.getGameID(), session);
            case MAKE_MOVE -> move(cmd.getAuthToken(), cmd.getGame(), cmd.getGameID(), cmd.getMove(), session);
//            case LEAVE -> ;
//            case RESIGN -> ;
        }
    }

    private void connect(String authToken, String color, Session session) throws DataAccessException, IOException {
        connections.add(authToken, session);
        String username = userAccess.getUser(authToken).username();

        String msg = "You just joined the game as " + ((color != null) ? color : "an observer");
        String notification = username + " just joined the game as " + ((color != null) ? color : "an observer");

        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, msg);
        connections.send(authToken, serverMsg);
        serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
        connections.broadcast(authToken, serverMsg);
    }

    private void move(String authToken, ChessGame game, int gameID, MoveCommand move,
                      Session session) throws DataAccessException, IOException {
        if (!authAccess.validateAuth(authToken)) {
            gameAccess.updateGameState(gameID, game);
            ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, "");
            serverMsg.setGame(game);
            connections.broadcast("", serverMsg);

            String username = userAccess.getUser(authToken).username();
            String notification = username + " moved " + move.start() + " to " + move.end();
            serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
            connections.broadcast(authToken, serverMsg);

            String enemy = (move.color().equals("white")) ? "Black" : "White";
            if (move.check() && !move.mate() && !move.stale()) { // check
                notification = enemy + " is in check!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                connections.broadcast("", serverMsg);
            } else if (move.check() && move.mate() && !move.stale()) { // checkmate
                notification = enemy + " is in checkmate!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                serverMsg.setGameOver(true);
                serverMsg.setWinner(move.color());
                connections.broadcast("", serverMsg);
            } else if (!move.check() && move.mate() && move.stale()) { // stalemate
                notification = "Stalemate!";
                serverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notification);
                serverMsg.setGameOver(true);
                connections.broadcast("", serverMsg);
            }
        }
    }
}
