package client;

import exceptions.ParentException;
import model.GameInfo;
import org.junit.jupiter.api.*;
import request.*;
import server.Server;
import server.ServerFacade;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws ParentException {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
        facade.clear();
    }

    @AfterEach
    public void clear() throws ParentException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void goodRegister() throws ParentException {
        var res = facade.register(new RegisterRequest("hi", "I'm", "boba"));
        assert(res.username().equals("hi"));
        assert(res.authToken() != null);
    }

    @Test
    public void badRegister() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.register(new RegisterRequest("hi", "I'm", "boba")));
        assertThrows(ParentException.class, () ->
                facade.register(new RegisterRequest(null, "I'm", "boba")));
        assertThrows(ParentException.class, () ->
                facade.register(new RegisterRequest("hi", null, "boba")));
        assertThrows(ParentException.class, () ->
                facade.register(new RegisterRequest("hi", "I'm", null)));
    }

    @Test
    public void goodLogin() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        var res = facade.login(new LoginRequest("hi", "I'm"));
        assert(res.username().equals("hi"));
        assert(res.authToken() != null);
    }

    @Test
    public void badLogin() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.login(new LoginRequest(null, "I'm")));
        assertThrows(ParentException.class, () ->
                facade.login(new LoginRequest("hi", null)));
        assertThrows(ParentException.class, () ->
                facade.login(new LoginRequest("I'm", "hi")));
    }

    @Test
    public void goodLogout() throws ParentException {
        var regRes = facade.register(new RegisterRequest("hi", "I'm", "boba"));

        var res = facade.logout(new LogoutRequest(regRes.authToken()));
        assert(res != null);
    }

    @Test
    public void badLogout() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.logout(new LogoutRequest("hi")));
        assertThrows(ParentException.class, () ->
                facade.logout(new LogoutRequest(null)));
    }

    @Test
    public void goodCreate() throws ParentException {
        var regRes = facade.register(new RegisterRequest("hi", "I'm", "boba"));

        var res = facade.create(new CreateRequest("test1", regRes.authToken()));
        assert(res.gameID() == 1);
    }

    @Test
    public void badCreate() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.create(new CreateRequest("hi", "hi")));
        assertThrows(ParentException.class, () ->
                facade.create(new CreateRequest(null, "hi")));
        assertThrows(ParentException.class, () ->
                facade.create(new CreateRequest("hi", null)));
    }

    @Test
    public void goodList() throws ParentException {
        var regRes = facade.register(new RegisterRequest("hi", "I'm", "boba"));
        facade.create(new CreateRequest("test1", regRes.authToken()));
        facade.create(new CreateRequest("test2", regRes.authToken()));
        facade.create(new CreateRequest("test3", regRes.authToken()));

        var games = facade.list(new ListRequest(regRes.authToken()));
        assert(games.games().contains(new GameInfo(1, null, null, "test1")));
        assert(games.games().contains(new GameInfo(2, null, null, "test2")));
        assert(games.games().contains(new GameInfo(3, null, null, "test3")));
    }

    @Test
    public void badList() throws ParentException {
        facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.list(new ListRequest("hi")));
        assertThrows(ParentException.class, () ->
                facade.list(new ListRequest(null)));
    }

    @Test
    public void goodJoin() throws ParentException {
        var regRes = facade.register(new RegisterRequest("hi", "I'm", "boba"));
        facade.create(new CreateRequest("test1", regRes.authToken()));

        var res = facade.join(new JoinRequest("white", 1, regRes.authToken()));
        assert(res.game() != null);
    }

    @Test
    public void badJoin() throws ParentException {
        var regRes = facade.register(new RegisterRequest("hi", "I'm", "boba"));

        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest("white", 1, regRes.authToken())));

        facade.create(new CreateRequest("test1", regRes.authToken()));

        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest(null, 1, regRes.authToken())));
        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest("white", 2, regRes.authToken())));
        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest("white", 1, null)));
        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest("gray", 1, regRes.authToken())));
        assertThrows(ParentException.class, () ->
                facade.join(new JoinRequest("white", 1, "hi")));

        facade.join(new JoinRequest("white", 1, regRes.authToken()));
        facade.join(new JoinRequest("black", 1, regRes.authToken()));
    }
}
