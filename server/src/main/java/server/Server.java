package server;

import dataaccess.DataAccessException;
import request.CreateRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import service.*;
import spark.*;
import com.google.gson.Gson;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::create);
//        Spark.get("/game", this::listGames);
//        Spark.put("/game", this::join);

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

    private Object clear(Request req, Response res) throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.clearAllData();
        return "";
    }

    private Object register(Request req, Response res) throws DataAccessException {
        var registerReq = new Gson().fromJson(req.body(), RegisterRequest.class);
        UserService userService = new UserService();
        return new Gson().toJson(userService.register(registerReq));
    }

    private Object login(Request req, Response res) throws DataAccessException {
        var loginReq = new Gson().fromJson(req.body(), LoginRequest.class);
        UserService userService = new UserService();
        return new Gson().toJson(userService.login(loginReq));
    }

    private Object logout(Request req, Response res) throws DataAccessException {
        LogoutRequest logoutReq = new LogoutRequest(req.headers("Authorization"));
        UserService userService = new UserService();
        return new Gson().toJson(userService.logout(logoutReq));
    }
}
