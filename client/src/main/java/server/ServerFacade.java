package server;

import com.google.gson.Gson;
import exceptions.ParentException;
import request.*;
import result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {
    private final String serverURL;

    public ServerFacade(int port) {
        this.serverURL = "http://localhost:" + port;
    }

    public ClearResult clear() throws ParentException {
        var path = "/db";
        return this.makeHTTP("DELETE", path, null, ClearResult.class, null);
    }

    public RegisterResult register(RegisterRequest req) throws ParentException {
        var path = "/user";
        return this.makeHTTP("POST", path, req, RegisterResult.class, null);
    }

    public LoginResult login(LoginRequest req) throws ParentException {
        var path = "/session";
        return this.makeHTTP("POST", path, req, LoginResult.class, null);
    }

    public LogoutResult logout(LogoutRequest req) throws ParentException {
        var path = "/session";
        return this.makeHTTP("DELETE", path, req, LogoutResult.class, req.authToken());
    }

    public CreateResult create(CreateRequest req) throws ParentException {
        var path = "/game";
        return this.makeHTTP("POST", path, req, CreateResult.class, req.authToken());
    }

    public ListResult list(ListRequest req) throws ParentException {
        var path = "/game";
        return this.makeHTTP("GET", path, req, ListResult.class, req.authToken());
    }

    public JoinResult join(JoinRequest req) throws ParentException {
        var path = "/game";
        return this.makeHTTP("PUT", path, req, JoinResult.class, req.authToken());
    }

    private <T> T makeHTTP(String method, String path, Object req, Class<T> resClass, String auth) throws ParentException {
        try {
            URL fullURL = (new URI(serverURL + path)).toURL();
            HttpURLConnection connection = (HttpURLConnection) fullURL.openConnection();
            connection.setRequestMethod(method);
            connection.setDoOutput(true);

            writeBody(req, connection, auth);

            System.out.println("Connecting...");
            connection.connect();
            System.out.println("Connected");

            System.out.println("Handling exceptions...");
            exceptionHandler(connection);
            System.out.println("Handler");

            System.out.println("Returning read response...");
            return readBody(connection, resClass);

        } catch (URISyntaxException | IOException e) {
            throw new ParentException(e.getMessage(), 500);
        }

    }

    private void writeBody(Object req, HttpURLConnection connection, String authToken) throws ParentException {
        connection.addRequestProperty("Content-Type", "application/json");
        if (authToken != null) {
            connection.addRequestProperty("authorization", authToken);
        }
        System.out.println(req);
        String reqData = new Gson().toJson(req);

        System.out.println(reqData);

        try (OutputStream reqBody = connection.getOutputStream()) {
            reqBody.write(reqData.getBytes());
            System.out.println("Written");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ParentException(e.getMessage(), 500);
        }
    }

    private <T> T readBody(HttpURLConnection connection, Class<T> resultClass) throws IOException {
        T result = null;

        if (connection.getContentLength() < 0) {
            try (InputStream resultBody = connection.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(resultBody);
                result =new Gson().fromJson(reader, resultClass);
            }
        }

        return result;
    }

    private void exceptionHandler(HttpURLConnection connection) throws IOException, ParentException {
        System.out.println(connection.getResponseCode());
        System.out.println(connection.getResponseMessage());
        var code = connection.getResponseCode();
        if (code / 100 != 2) {
            try (InputStream exception = connection.getErrorStream()) {
                if (exception != null) {
                    var map = new Gson().fromJson(new InputStreamReader(exception), HashMap.class);
                    System.out.println(map);
                    String message = map.get("message").toString();
                    throw new ParentException(message, code);
                }
            }
        }
    }
}
