package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import exceptions.ParentException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    private final Session session;
    private final NotificationHandler handler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ParentException {
        try {
            url = url.replace("http", "ws");
            URI socketURI =new URI(url + "/ws");
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.handler = notificationHandler;

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage msg = new Gson().fromJson(message, ServerMessage.class);
                    handler.notify(msg);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ParentException(e.getMessage(), 500);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void joinGame(String authToken, String color, int gameID) throws ParentException {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        command.setColor(color);
        sendCmd(command);
    }

    public void makeMove(String authToken, int gameID, ChessMove move, String color) throws ParentException {
        var command = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
        command.setMove(move);
        command.setColor(color);
//        command.setGame(game);
        sendCmd(command);
    }

    public void leave(String authToken, int gameID, String color) throws ParentException {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        command.setColor(color);
//            command.setGame(game);
        sendCmd(command);
//            session.close();
    }

    public void resign(String authToken, int gameID) throws ParentException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
//        command.setColor(color);
//        command.setGame(game);
        sendCmd(command);
    }

    private void sendCmd(UserGameCommand cmd) throws ParentException {
        try {
            this.session.getBasicRemote().sendText(new Gson().toJson(cmd));
        } catch (IOException ex) {
            throw new ParentException(ex.getMessage(), 500);
        }
    }
}
