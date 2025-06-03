package server;

import request.*;
import result.*;

public class ServerFacade {
    private final int port;
    private final String serverURL;
    private final String authToken;

    public ServerFacade(int port) {
        this.port = port;
        this.serverURL = "http://localhost:" + port;
        this.authToken = null;
    }

    public ClearResult clear() {
        var path = "/db";
    }

    public RegisterResult register(RegisterRequest req) {
        var path = "/user";
    }

    public LoginResult login(LoginRequest req) {
        var path = "/session";
    }

    public LogoutResult logout(LoginRequest req) {
        var path = "/session";
    }

    public CreateResult create(CreateRequest req) {
        var path = "/game";
    }

    public ListResult list(ListRequest req) {
        var path = "/game";
    }

    public JoinResult join(JoinRequest req) {
        var path = "/game";
    }

    private Object makeRquest(String method, String path, Object request, Object responseClass) {

    }
}
