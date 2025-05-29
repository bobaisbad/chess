package dataaccess;

import exceptions.*;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameInfo;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseUnitTests {

    private final AuthDAO authAccess = new AuthDatabaseAccess();
    private final UserDAO userAccess = new UserDatabaseAccess();
    private final GameDAO gameAccess = new GameDatabaseAccess();

    @BeforeEach
    void clearDatabase() throws ParentException {
        authAccess.deleteAllAuth();
        userAccess.deleteAllUsers();
        gameAccess.deleteAllGames();
    }

    @Test
    void goodAuthCreate() throws ParentException, SQLException {
        authAccess.createAuth("boba");

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "boba");

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert (result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    void checkGame() throws DataAccessException, SQLException {
        String stmt = "SELECT gameID, whiteUsername, blackUsername, gameName, game " +
                      "FROM games " +
                      "WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setInt(1, 1);
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        int gameID = result.getInt("gameID");
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");
                        String gameName = result.getString("gameName");
                        ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);

                        assert(1 == gameID);
                        assert(whiteUsername == null);
                        assert(blackUsername == null);
                        assert("test1".equals(gameName));
                        assert(game.getBoard() instanceof ChessBoard);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    void checkTotal(String table, int cnt) throws DataAccessException, SQLException {
        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM " + table;

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert (result.getInt("total") == cnt);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    @Test
    void badAuthCreate() {
        assertThrows(DataAccessException.class, () ->
                authAccess.createAuth(null));
    }

    @Test
    void goodAuthDelete() throws ParentException, SQLException {
        AuthData data = authAccess.createAuth("boba");
        authAccess.deleteAuth(data.authToken());

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, data.username());

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert (result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    @Test
    void badAuthDelete() throws ParentException, SQLException {
        authAccess.createAuth("boba");

        String token = UUID.randomUUID().toString();
        authAccess.deleteAuth(token);
        authAccess.deleteAuth(null);

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths " +
                      "WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, token);

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert (result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    @Test
    void goodAuthValidate() throws ParentException {
        AuthData data = authAccess.createAuth("boba");
        assert(!authAccess.validateAuth(data.authToken()));
    }

    @Test
    void badAuthValidate() throws ParentException {
        authAccess.createAuth("boba");
        assert(authAccess.validateAuth(UUID.randomUUID().toString()));
        assert(authAccess.validateAuth(null));
    }

    @Test
    void goodUsernameGet() throws ParentException {
        AuthData data = authAccess.createAuth("boba");

        assert(authAccess.getUsername(data.authToken()).equals("boba"));
    }

    @Test
    void badUsernameGet() throws ParentException {
        authAccess.createAuth("boba");

        assert(authAccess.getUsername(null) == null);
        assert(authAccess.getUsername(UUID.randomUUID().toString()) == null);
    }

    @Test
    void goodAuthClear() throws ParentException, SQLException {
        authAccess.createAuth("boba");
        authAccess.createAuth("is");
        authAccess.createAuth("baddest");
        authAccess.deleteAllAuth();

        checkTotal("auths", 0);
    }

    @Test
    void goodUserGet() throws ParentException {
        userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com"));
        UserData data = userAccess.getUser("boba");
        assert(data.username().equals("boba"));
        assert(BCrypt.checkpw("pass", data.password()));
        assert(data.email().equals("hi@gmail.com"));
    }

    @Test
    void badUserGet() throws ParentException {
        userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com"));
        assert(userAccess.getUser(null) == null);
        assert(userAccess.getUser("hi") == null);
    }

    @Test
    void goodUserCreate() throws ParentException, SQLException {
        userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com"));

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM users " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "boba");
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert (result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    @Test
    void badUserCreate() throws ParentException {
        userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com"));
        assertThrows(DataAccessException.class, () ->
                userAccess.createUser(new RegisterRequest(null, "pass", "hi@gmail.com")));
        assertThrows(DataAccessException.class, () ->
                userAccess.createUser(new RegisterRequest("baddest", "pass", null)));
        assertThrows(DataAccessException.class, () ->
                userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com")));
    }

    @Test
    void goodUserClear() throws ParentException, SQLException {
        userAccess.createUser(new RegisterRequest("boba", "pass", "hi@gmail.com"));
        userAccess.createUser(new RegisterRequest("is", "pass", "hi@gmail.com"));
        userAccess.createUser(new RegisterRequest("baddest", "pass", "hi@gmail.com"));
        userAccess.deleteAllUsers();

        checkTotal("users", 0);
    }

    @Test
    void goodGameCreate() throws ParentException, SQLException {
        gameAccess.createGame("test1");

        checkGame();
    }

    @Test
    void badGameCreate() throws ParentException, SQLException {
        gameAccess.createGame("test1");
        assertThrows(DataAccessException.class, () ->
                gameAccess.createGame(null));

        checkTotal("games", 1);
    }

    @Test
    void goodGameGet() throws ParentException {
        gameAccess.createGame("test1");
        GameData data = gameAccess.getGame(1);

        assert(data.gameID() == 1);
        assert(data.whiteUsername() == null);
        assert(data.blackUsername() == null);
        assert(data.gameName().equals("test1"));
        assert(data.game() instanceof ChessGame);
    }

    @Test
    void badGameGet() throws ParentException {
        gameAccess.createGame("test1");

        assert(gameAccess.getGame(2) == null);
    }

    @Test
    void goodGameList() throws ParentException {
        gameAccess.createGame("test1");
        gameAccess.createGame("test2");
        gameAccess.createGame("test3");
        Collection<GameInfo> games = gameAccess.listGames();

        assert(games.size() == 3);
        assert(games.contains(new GameInfo(1, null, null, "test1")));
        assert(games.contains(new GameInfo(2, null, null, "test2")));
        assert(games.contains(new GameInfo(3, null, null, "test3")));
    }

    @Test
    void badGameList() throws ParentException {
        Collection<GameInfo> games = gameAccess.listGames();
        assert(games.isEmpty());
    }

    @Test
    void goodGameUpdate() throws ParentException, SQLException {
        gameAccess.createGame("test1");
        GameData data = gameAccess.getGame(1);
        gameAccess.updateGame(data, "WHITE", "boba");

        String stmt = "SELECT whiteUsername, blackUsername " +
                      "FROM games " +
                      "WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setInt(1, 1);
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");

                        assert(whiteUsername.equals("boba"));
                        assert(blackUsername == null);
                    } else {
                        assert false;
                    }
                }
            }
        }
    }

    @Test
    void badGameUpdate() throws ParentException, SQLException {
        gameAccess.createGame("test1");
        GameData data = new GameData(2, null, null, "test1", new ChessGame());
        gameAccess.updateGame(data, "WHITE", "boba");

        checkGame();
    }

    @Test
    void goodGameClear() throws ParentException, SQLException {
        gameAccess.createGame("test1");
        gameAccess.createGame("test2");
        gameAccess.createGame("test3");
        gameAccess.deleteAllGames();

        checkTotal("games", 0);
    }
}
