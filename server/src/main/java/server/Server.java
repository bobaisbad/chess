package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", (req, res) -> "Insert clear code here");
        // Spark.delete("/db", this::clear);
        Spark.post("/user", (req, res) -> "Insert register code here");
//        Spark.post("/user", this::register);
        Spark.post("/session", (req, res) -> "Insert login code here");
//        Spark.post("/session", this::login);
        Spark.delete("/session", (req, res) -> "Insert logout code here");
//        Spark.delete("/session", this::logout);
        Spark.get("/game", (req, res) -> "Insert list code here");
//        Spark.get("/game", this::listGames);
        Spark.post("/game", (req, res) -> "Insert create code here");
//        Spark.post("/game", this::create);
        Spark.put("/game", (req, res) -> "Insert join code here");
//        Spark.put("/game", this::join);

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
