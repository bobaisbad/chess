package server;

import Exceptions.*;
import dataaccess.*;
import model.ExceptionInfo;
import request.*;
import result.ClearResult;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public Server() {
        String service = "db";
        AuthDAO authAccess;
        UserDAO userAccess;
        GameDAO gameAccess;

        if (service.equals("db")) {
            authAccess = new AuthDatabaseAccess();
            userAccess = new UserDatabaseAccess();
            gameAccess = new GameDatabaseAccess();
        } else {
            authAccess = new AuthMemoryDataAccess();
            userAccess = new UserMemoryDataAccess();
            gameAccess = new GameMemoryDataAccess();
        }

        this.userService = new UserService(authAccess, userAccess);
        this.gameService = new GameService(authAccess, gameAccess);
        this.clearService = new ClearService(authAccess, gameAccess, userAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::create);
        Spark.get("/game", this::list);
        Spark.put("/game", this::join);
        Spark.exception(ParentException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ParentException ex, Request req, Response res) {
        res.status(ex.statusCode());
        ExceptionInfo info = new ExceptionInfo(ex.getMessage());
        res.body(new Gson().toJson(info));
    }

    private Object clear(Request req, Response res) throws ParentException {
        clearService.clearAllData();
        return new Gson().toJson(new ClearResult());
    }

    private Object register(Request req, Response res) throws ParentException {
        var registerReq = new Gson().fromJson(req.body(), RegisterRequest.class);
        return new Gson().toJson(userService.register(registerReq));
    }

    private Object login(Request req, Response res) throws ParentException {
        var loginReq = new Gson().fromJson(req.body(), LoginRequest.class);
        return new Gson().toJson(userService.login(loginReq));
    }

    private Object logout(Request req, Response res) throws ParentException {
        LogoutRequest logoutReq = new LogoutRequest(req.headers("authorization"));
        return new Gson().toJson(userService.logout(logoutReq));
    }

    private Object create(Request req, Response res) throws ParentException {
        var createReq1 = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateRequest createReq2 = new CreateRequest(createReq1.gameName(), req.headers("authorization"));
        return new Gson().toJson(gameService.create(createReq2));
    }

    private Object list(Request req, Response res) throws ParentException {
        ListRequest listReq = new ListRequest(req.headers("authorization"));
        return new Gson().toJson(gameService.list(listReq));
    }

    private Object join(Request req, Response res) throws ParentException {
        var joinReq1 = new Gson().fromJson(req.body(), JoinRequest.class);
        JoinRequest joinReq2 = new JoinRequest(joinReq1.playerColor(), joinReq1.gameID(), req.headers("authorization"));
        return new Gson().toJson(gameService.join(joinReq2));
    }
}
