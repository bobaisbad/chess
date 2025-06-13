package websocket;

import com.google.gson.Gson;
import exceptions.ParentException;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Session session, int gameID, String username) {
        Connection connection = new Connection(authToken, session, gameID, username);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
//        if (connections.get(authToken).getSession().isOpen()) {
////            connections.get(authToken).getSession().close();
//        }
        connections.remove(authToken);
    }

    public void broadcast(int gameID, String authToken, ServerMessage msg) throws IOException {
        ArrayList<Connection> remove = new ArrayList<>();

        for (Connection c : connections.values()) {
            if (c.getSession().isOpen()) {
                if (!c.getAuthToken().equals(authToken) && c.getGameID() == gameID) {
                    c.send(new Gson().toJson(msg));
                }
            } else {
                remove.add(c);
            }
        }

        for (Connection c : remove) {
            connections.remove(c.getAuthToken());
        }
    }

    public void send(String authToken, Session session, ServerMessage msg) throws IOException, ParentException {
        Connection c = connections.get(authToken);

        if (c == null) {
            for (var con : connections.values()) {
                if (con.getSession() == session) {
                    c = con;
                    break;
                }
            }

            if (c == null) {
                return;
            }
        }

        if (c.getSession().isOpen()) {
            c.send(new Gson().toJson(msg));
        } else {
            connections.remove(c.getAuthToken());
        }
    }
}
