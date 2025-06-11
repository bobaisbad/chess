package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {

    private final String authToken;
    private final Session session;

    public Connection (String authToken, Session session) {
        this.authToken = authToken;
        this.session = session;
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
}
