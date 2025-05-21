package server;

import dataaccess.*;
import model.ExceptionInfo;
import request.*;
import result.ClearResult;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {
//    private final GameMemoryDataAccess gameAccess = new GameMemoryDataAccess();
//    private final UserMemoryDataAccess userAccess = new UserMemoryDataAccess();
    private final AuthDAO authAccess = new AuthMemoryDataAccess();
    private final UserService userService = new UserService(authAccess);
    private final GameService gameService = new GameService(authAccess);
    private final ClearService clearService = new ClearService(authAccess, gameService.getGameAccess(), userService.getUserAccess());

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
        Spark.exception(UnauthorizedException.class, this::exceptionHandler);
        Spark.exception(BadRequestException.class, this::exceptionHandler);
        Spark.exception(TakenException.class, this::exceptionHandler);


//        Spark.put("/game", (req, res) -> "Insert join code here");
//        Spark.post("/game", (req, res) -> "Insert create code here");
//        Spark.get("/game", (req, res) -> "Insert list code here");
//        Spark.delete("/session", (req, res) -> "Insert logout code here");
//        Spark.post("/session", (req, res) -> "Insert login code here");
//        Spark.post("/user", (req, res) -> "Insert register code here");
//        Spark.delete("/db", (req, res) -> "Insert clear code here");

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ParentException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        ExceptionInfo info = new ExceptionInfo(ex.getMessage());
        res.body(new Gson().toJson(info));
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        clearService.clearAllData();
        return new Gson().toJson(new ClearResult());
    }

    private Object register(Request req, Response res) throws DataAccessException, TakenException, BadRequestException {
        var registerReq = new Gson().fromJson(req.body(), RegisterRequest.class);
//        UserService userService = new UserService();
        return new Gson().toJson(userService.register(registerReq));
    }

    private Object login(Request req, Response res) throws DataAccessException, BadRequestException, UnauthorizedException {
        var loginReq = new Gson().fromJson(req.body(), LoginRequest.class);
//        UserService userService = new UserService();
        return new Gson().toJson(userService.login(loginReq));
    }

    private Object logout(Request req, Response res) throws DataAccessException, UnauthorizedException {
        LogoutRequest logoutReq = new LogoutRequest(req.headers("authorization"));
//        UserService userService = new UserService();
        return new Gson().toJson(userService.logout(logoutReq));
    }

    private Object create(Request req, Response res) throws DataAccessException, UnauthorizedException {
        var createReq1 = new Gson().fromJson(req.body(), CreateRequest.class);
        CreateRequest createReq2 = new CreateRequest(createReq1.gameName(), req.headers("authorization"));
//        GameService gameService = new GameService();
        return new Gson().toJson(gameService.create(createReq2));
    }

    private Object list(Request req, Response res) throws DataAccessException, UnauthorizedException {
        ListRequest listReq = new ListRequest(req.headers("authorization"));
//        GameService gameService = new GameService();
        return new Gson().toJson(gameService.list(listReq));
    }

    private Object join(Request req, Response res) throws DataAccessException, UnauthorizedException, BadRequestException, TakenException {
        var joinReq1 = new Gson().fromJson(req.body(), JoinRequest.class);
        JoinRequest joinReq2 = new JoinRequest(joinReq1.playerColor(), joinReq1.gameID(), req.headers("authorization"));
//        GameService gameService = new GameService();
        return new Gson().toJson(gameService.join(joinReq2));
    }
}
