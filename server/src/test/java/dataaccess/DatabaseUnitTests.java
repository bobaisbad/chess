package dataaccess;

import Exceptions.*;
import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import model.GameInfo;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import result.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DatabaseUnitTests {

    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public DatabaseUnitTests() throws DataAccessException {
        new DatabaseManager();
        AuthDAO authAccess = new AuthDatabaseAccess();
        UserDAO userAccess = new UserDatabaseAccess();
        GameDAO gameAccess = new GameDatabaseAccess();

        this.userService = new UserService(authAccess, userAccess);
        this.gameService = new GameService(authAccess, gameAccess);
        this.clearService = new ClearService(authAccess, gameAccess, userAccess);
    }

    @BeforeEach
    void clearAll() throws ParentException {
        clearService.clearAllData();
    }

    @Test
    void goodRegister() throws ParentException {
        var user = new RegisterRequest("boba", "pass", "hello@byu.edu");
        userService.register(user);
        var data = new UserData("boba", "pass", "hello@byu.edu");

        String stmt = "SELECT username, password, email " +
                      "FROM users " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "boba");
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        String username = result.getString("username");
                        String password = result.getString("password");
                        String email = result.getString("email");

                        assert(data.username().equals(username));
                        assert(BCrypt.checkpw(data.password(), password));
                        assert(data.email().equals(email));
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve user", 500);
        }
    }

    @Test
    void badRegister() throws ParentException {
        var user = new RegisterRequest("boba", "pass", "hello@byu.edu");
        userService.register(user);
        var badUser1 = new RegisterRequest(null, "pass", "hello@byu.edu");
        var badUser2 = new RegisterRequest("is", null, "hello@byu.edu");
        var badUser3 = new RegisterRequest("baddest", "pass", null);
        var copyUser = new RegisterRequest("boba", "pass", "hello@byu.edu");

        assertThrows(BadRequestException.class, () ->
                userService.register(badUser1));
        assertThrows(BadRequestException.class, () ->
                userService.register(badUser2));
        assert(userService.getUserAccess().getUser("is") == null);
        assertThrows(BadRequestException.class, () ->
                userService.register(badUser3));
        assert(userService.getUserAccess().getUser("baddest") == null);
        assertThrows(TakenException.class, () ->
                userService.register(copyUser));
        assert(userService.getUserAccess().getUser("boba") != null);

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM users " +
                      "WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "boba");

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, null);

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "is");

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, "baddest");

                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve user", 500);
        }
    }

    @Test
    void goodLogin() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        userService.logout(new LogoutRequest(reqResult.authToken()));

        var user = new LoginRequest("boba", "pass");
        LoginResult loginResult = userService.login(user);

        String stmt = "SELECT authToken, username " +
                      "FROM auths " +
                      "WHERE authToken = ?";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                prepStmt.setString(1, loginResult.authToken());
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        String authToken = result.getString("authToken");
                        String username = result.getString("username");

                        assert(loginResult.authToken().equals(authToken));
                        assert(loginResult.username().equals(username));
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve AuthData", 500);
        }
    }

    @Test
    void badLogin() throws ParentException {
        userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));

        var badUser1 = new LoginRequest("boba", null);
        var badUser2 = new LoginRequest(null, "pass");
        var nonExistentUser = new LoginRequest("hello", "goodbye");

        assertThrows(BadRequestException.class, () ->
                userService.login(badUser1));
        assertThrows(BadRequestException.class, () ->
                userService.login(badUser2));
        assertThrows(UnauthorizedException.class, () ->
                userService.login(nonExistentUser));

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve AuthData", 500);
        }
    }

    @Test
    void goodLogout() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));

        var goodUser = new LogoutRequest(reqResult.authToken());
        userService.logout(goodUser);

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve AuthData", 500);
        }
    }

    @Test
    void badLogout() throws ParentException {
        userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));

        var badUSer = new LogoutRequest(UUID.randomUUID().toString());

        assertThrows(UnauthorizedException.class, () ->
                userService.logout(badUSer));

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM auths";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve AuthData", 500);
        }
    }

    @Test
    void goodCreate() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        GameInfo info = new GameInfo(1, null, null, "test1");

        var goodGame = new CreateRequest("test1", reqResult.authToken());
        gameService.create(goodGame);

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

                        assert(info.gameID() == gameID);
                        assert(whiteUsername == null);
                        assert(blackUsername == null);
                        assert(info.gameName().equals(gameName));
                        assert(game.getBoard() instanceof ChessBoard);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve GameData", 500);
        }
    }

    @Test
    void badCreate() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);

        var goodGame = new CreateRequest("test1", reqResult.authToken());
        var badGame1 = new CreateRequest(null, reqResult.authToken());
        var badGame2 = new CreateRequest("test2", UUID.randomUUID().toString());
        var badGame3 = new CreateRequest("test3", null);

        gameService.create(goodGame);
        assertThrows(BadRequestException.class, () ->
                gameService.create(badGame1));
        assertThrows(UnauthorizedException.class, () ->
                gameService.create(badGame2));
        assertThrows(UnauthorizedException.class, () ->
                gameService.create(badGame3));

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM games";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 1);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve GameData", 500);
        }
    }

    @Test
    void goodList() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        gameService.create(new CreateRequest("test1", reqResult.authToken()));
        GameInfo goodInfo1 = new GameInfo(1, null, null, "test1");
        gameService.create(new CreateRequest("test2", reqResult.authToken()));
        GameInfo goodInfo2 = new GameInfo(2, null, null, "test2");
        gameService.create(new CreateRequest("test3", reqResult.authToken()));
        GameInfo goodInfo3 = new GameInfo(3, null, null, "test3");
        GameInfo[] goodGames = {goodInfo1, goodInfo2, goodInfo3};
        int cnt = 0;

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM games";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 3);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve GameData", 500);
        }

        stmt = "SELECT gameID, whiteUsername, blackUsername, gameName, game " +
               "FROM games";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    while (result.next()) {
                        int gameID = result.getInt("gameID");
                        String whiteUsername = result.getString("whiteUsername");
                        String blackUsername = result.getString("blackUsername");
                        String gameName = result.getString("gameName");
                        ChessGame game = new Gson().fromJson(result.getString("game"), ChessGame.class);

                        assert(goodGames[cnt].gameID() == gameID);
                        assert(whiteUsername == null);
                        assert(blackUsername == null);
                        assert(goodGames[cnt].gameName().equals(gameName));
                        assert(game.getBoard() instanceof ChessBoard);

                        cnt++;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve GameData", 500);
        }
    }

    @Test
    void badList() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        gameService.create(new CreateRequest("test1", reqResult.authToken()));
        gameService.create(new CreateRequest("test2", reqResult.authToken()));
        gameService.create(new CreateRequest("test3", reqResult.authToken()));

        ListRequest badList1 = new ListRequest(null);
        ListRequest badList2 = new ListRequest(UUID.randomUUID().toString());

        assertThrows(UnauthorizedException.class, () ->
                gameService.list(badList1));
        assertThrows(UnauthorizedException.class, () ->
                gameService.list(badList2));

        String stmt = "SELECT COUNT(*) AS total " +
                      "FROM games";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 3);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve GameData", 500);
        }
    }

    @Test
    void goodJoin() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        gameService.create(new CreateRequest("test1", reqResult.authToken()));

        JoinRequest goodJoin = new JoinRequest("WHITE", 1, reqResult.authToken());
        gameService.join(goodJoin);

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

                        assert(goodJoin.gameID() == gameID);
                        assert(whiteUsername.equals("boba"));
                        assert(blackUsername == null);
                        assert(gameName.equals("test1"));
                        assert(game.getBoard() instanceof ChessBoard);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve GameData", 500);
        }
    }

    @Test
    void badJoin() throws ParentException {
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        gameService.create(new CreateRequest("test1", reqResult.authToken()));

        JoinRequest badJoin1 = new JoinRequest(null, 1, reqResult.authToken());
        JoinRequest badJoin2 = new JoinRequest("WHITE", 0, reqResult.authToken());
        JoinRequest badJoin3 = new JoinRequest("WHITE", 1, null);
        JoinRequest badJoin4 = new JoinRequest("WHITE", 1, UUID.randomUUID().toString());
        JoinRequest badJoin5 = new JoinRequest("WHITE", 2, reqResult.authToken());
        JoinRequest badJoin6 = new JoinRequest("HELLO", 1, reqResult.authToken());

        assertThrows(BadRequestException.class, () ->
                gameService.join(badJoin1));
        assertThrows(BadRequestException.class, () ->
                gameService.join(badJoin2));
        assertThrows(UnauthorizedException.class, () ->
                gameService.join(badJoin3));
        assertThrows(UnauthorizedException.class, () ->
                gameService.join(badJoin4));
        assertThrows(BadRequestException.class, () ->
                gameService.join(badJoin5));
        assertThrows(BadRequestException.class, () ->
                gameService.join(badJoin6));

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

                        assert(gameID == 1);
                        assert(whiteUsername == null);
                        assert(blackUsername == null);
                        assert(gameName.equals("test1"));
                        assert(game.getBoard() instanceof ChessBoard);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve GameData", 500);
        }

        gameService.join(new JoinRequest("WHITE", 1, reqResult.authToken()));
        gameService.join(new JoinRequest("BLACK", 1, reqResult.authToken()));

        JoinRequest badJoin7 = new JoinRequest("WHITE", 1, reqResult.authToken());
        JoinRequest badJoin8 = new JoinRequest("BLACK", 1, reqResult.authToken());

        assertThrows(TakenException.class, () ->
                gameService.join(badJoin7));
        assertThrows(TakenException.class, () ->
                gameService.join(badJoin8));

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

                        assert(gameID == 1);
                        assert(whiteUsername.equals("boba"));
                        assert(blackUsername.equals("boba"));
                        assert(gameName.equals("test1"));
                        assert(game.getBoard() instanceof ChessBoard);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: failed to retrieve GameData", 500);
        }
    }

    @Test
    void clear() throws ParentException {
        userService.register(new RegisterRequest("is", "pass", "gutentag@byu.edu"));
        userService.register(new RegisterRequest("baddest", "pass", "salut@byu.edu"));
        RegisterRequest registerUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        RegisterResult reqResult = userService.register(registerUser);
        gameService.create(new CreateRequest("test1", reqResult.authToken()));
        gameService.create(new CreateRequest("test2", reqResult.authToken()));
        gameService.create(new CreateRequest("test3", reqResult.authToken()));

        clearService.clearAllData();
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("boba", "pass")));
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("is", "pass")));
        assertThrows(UnauthorizedException.class, () ->
                userService.login(new LoginRequest("baddest", "pass")));
        assertThrows(UnauthorizedException.class, () ->
                gameService.list(new ListRequest(reqResult.authToken())).games().isEmpty());

        String stmt1 = "SELECT COUNT(*) AS total " +
                       "FROM games";
        String stmt2 = "SELECT COUNT(*) AS total " +
                       "FROM users";
        String stmt3 = "SELECT COUNT(*) AS total " +
                       "FROM auths";

        try (Connection conn = DatabaseManager.getConnection()) {
            try (var prepStmt = conn.prepareStatement(stmt1)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
            try (var prepStmt = conn.prepareStatement(stmt2)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
            try (var prepStmt = conn.prepareStatement(stmt3)) {
                try (ResultSet result = prepStmt.executeQuery()) {
                    if (result.next()) {
                        assert(result.getInt("total") == 0);
                    } else {
                        assert false;
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new DataAccessException("Error: unable to retrieve data", 500);
        }
    }
}
