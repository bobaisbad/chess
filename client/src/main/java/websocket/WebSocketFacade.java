package websocket;

import com.google.gson.Gson;
import exceptions.ParentException;
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
        try {
            var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            command.setColor(color);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ParentException(ex.getMessage(), 500);
        }
    }
}
