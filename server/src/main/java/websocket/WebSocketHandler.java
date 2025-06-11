package websocket;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import dataaccess.UserDatabaseAccess;
import exceptions.DataAccessException;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserDAO userAccess;

    public WebSocketHandler(UserDAO userAccess) {
        this.userAccess = userAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        switch (cmd.getCommandType()) {
            case CONNECT -> connect(cmd.getAuthToken(), session);
//            case MAKE_MOVE -> ;
//            case LEAVE -> ;
//            case RESIGN -> ;
        }
    }

    private void connect(String authToken, Session session) throws DataAccessException, IOException {
        connections.add(authToken, session);
        String username = userAccess.getUser(authToken).username();
        String msg = username + " just joined the game";
        ServerMessage serverMsg = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, msg);
        connections.broadcast(authToken, serverMsg);
    }
}
