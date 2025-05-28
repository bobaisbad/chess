package service;

import exceptions.*;
import dataaccess.*;
import model.GameInfo;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import request.*;
import result.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTests {
    private final UserService userService;
    private final GameService gameService;
    private final ClearService clearService;

    public ServiceTests() {
        AuthDAO authAccess = new AuthMemoryDataAccess();
        UserDAO userAccess = new UserMemoryDataAccess();
        GameDAO gameAccess = new GameMemoryDataAccess();

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
        var result = userService.register(user);
        var data = new UserData("boba", "pass", "hello@byu.edu");

        assert(result.username().equals("boba"));
        UserData result2 = userService.getUserAccess().getUser("boba");
        assert(result2.username().equals(data.username()));
        assert(BCrypt.checkpw(data.password(), result2.password()));
        assert(result2.email().equals(data.email()));
    }

    @Test
    void badRegister() throws ParentException {
        var user = new RegisterRequest("boba", "pass", "hello@byu.edu");
        var badUser1 = new RegisterRequest(null, "pass", "hello@byu.edu");
        var badUser2 = new RegisterRequest("is", null, "hello@byu.edu");
        var badUser3 = new RegisterRequest("baddest", "pass", null);
        var copyUser = new RegisterRequest("boba", "pass", "hello@byu.edu");
        var data = new UserData("boba", "pass", "hello@byu.edu");

        assert(userService.register(user).username().equals("boba"));
        UserData result = userService.getUserAccess().getUser("boba");
        assert(result.username().equals(data.username()));
        assert(BCrypt.checkpw(data.password(), result.password()));
        assert(result.email().equals(data.email()));
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
    }

    @Test
    void goodLogin() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        userService.logout(new LogoutRequest(reqResult.authToken()));

        var user = new LoginRequest("boba", "pass");
        LoginResult result = userService.login(user);

        assert(result.username().equals("boba"));
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
    }

    @Test
    void goodLogout() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));

        var goodUser = new LogoutRequest(reqResult.authToken());
        LogoutResult result = userService.logout(goodUser);

        assert(result != null);
    }

    @Test
    void badLogout() throws ParentException {
        userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));

        var badUSer = new LogoutRequest(UUID.randomUUID().toString());

        assertThrows(UnauthorizedException.class, () ->
            userService.logout(badUSer));
    }

    @Test
    void goodCreate() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        GameInfo info = new GameInfo(1, null, null, "test1");

        var goodGame = new CreateRequest("test1", reqResult.authToken());
        CreateResult result = gameService.create(goodGame);
        assert(result.gameID() == 1);
        assert(gameService.list(new ListRequest(reqResult.authToken())).games().contains(info));
    }

    @Test
    void badCreate() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        GameInfo info = new GameInfo(1, null, null, "test1");

        var goodGame = new CreateRequest("test1", reqResult.authToken());
        var badGame1 = new CreateRequest(null, reqResult.authToken());
        var badGame2 = new CreateRequest("test2", UUID.randomUUID().toString());
        var badGame3 = new CreateRequest("test3", null);

        gameService.create(goodGame);
        assert(gameService.list(new ListRequest(reqResult.authToken())).games().contains(info));
        assertThrows(BadRequestException.class, () ->
            gameService.create(badGame1));
        assertThrows(UnauthorizedException.class, () ->
            gameService.create(badGame2));
        assertThrows(UnauthorizedException.class, () ->
            gameService.create(badGame3));
    }

    @Test
    void goodList() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        gameService.create(new CreateRequest("test1", reqResult.authToken()));
        GameInfo goodInfo1 = new GameInfo(1, null, null, "test1");
        gameService.create(new CreateRequest("test2", reqResult.authToken()));
        GameInfo goodInfo2 = new GameInfo(2, null, null, "test2");
        gameService.create(new CreateRequest("test3", reqResult.authToken()));
        GameInfo goodInfo3 = new GameInfo(3, null, null, "test3");

        ListRequest goodList = new ListRequest(reqResult.authToken());
        ListResult result = gameService.list(goodList);
        assert(result.games().contains(goodInfo1));
        assert(result.games().contains(goodInfo2));
        assert(result.games().contains(goodInfo3));
        assert(result.games().size() == 3);
    }

    @Test
    void badList() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        gameService.create(new CreateRequest("test1", reqResult.authToken()));
        gameService.create(new CreateRequest("test2", reqResult.authToken()));
        gameService.create(new CreateRequest("test3", reqResult.authToken()));

        ListRequest badList1 = new ListRequest(null);
        ListRequest badList2 = new ListRequest(UUID.randomUUID().toString());

        assertThrows(UnauthorizedException.class, () ->
            gameService.list(badList1));
        assertThrows(UnauthorizedException.class, () ->
            gameService.list(badList2));
    }

    @Test
    void goodJoin() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
        gameService.create(new CreateRequest("test1", reqResult.authToken()));

        JoinRequest goodJoin = new JoinRequest("WHITE", 1, reqResult.authToken());
        JoinResult result = gameService.join(goodJoin);
        ListResult listRes = gameService.list(new ListRequest(reqResult.authToken()));
        GameInfo info = new GameInfo(1, "boba", null, "test1");
        assert(result != null);
        assert(listRes.games().contains(info));

    }

    @Test
    void badJoin() throws ParentException {
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
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

        gameService.join(new JoinRequest("WHITE", 1, reqResult.authToken()));
        gameService.join(new JoinRequest("BLACK", 1, reqResult.authToken()));

        JoinRequest badJoin7 = new JoinRequest("WHITE", 1, reqResult.authToken());
        JoinRequest badJoin8 = new JoinRequest("BLACK", 1, reqResult.authToken());

        assertThrows(TakenException.class, () ->
            gameService.join(badJoin7));
        assertThrows(TakenException.class, () ->
            gameService.join(badJoin8));

        ListResult listRes = gameService.list(new ListRequest(reqResult.authToken()));
        GameInfo info = new GameInfo(1, "boba", "boba", "test1");
        assert(listRes.games().contains(info));
    }

    @Test
    void clear() throws ParentException {
        userService.register(new RegisterRequest("is", "pass", "gutentag@byu.edu"));
        userService.register(new RegisterRequest("baddest", "pass", "salut@byu.edu"));
        RegisterResult reqResult = userService.register(new RegisterRequest("boba", "pass", "hello@byu.edu"));
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
        assert(userService.getUserAccess().getUser("boba") == null);
        assert(userService.getUserAccess().getUser("is") == null);
        assert(userService.getUserAccess().getUser("baddest") == null);
        assertThrows(UnauthorizedException.class, () ->
            gameService.list(new ListRequest(reqResult.authToken())).games().isEmpty());
    }
}
