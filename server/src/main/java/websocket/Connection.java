package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    private final String authToken;
    private final Session session;
    private final int gameID;
    private final String username;

    public Connection (String authToken, Session session, int gameID, String username) {
        this.authToken = authToken;
        this.session = session;
        this.gameID = gameID;
        this.username = username;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }

    public String getAuthToken() {
        return this.authToken;
    }

    public Session getSession() {
        return this.session;
    }

    public int getGameID() {
        return this.gameID;
    }

    public String getUsername() {
        return this.username;
    }
}
