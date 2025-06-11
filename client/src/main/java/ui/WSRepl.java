package ui;

import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

//import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class WSRepl implements NotificationHandler {
    private final ChessClient client;

    public WSRepl(ChessClient client) {
        this.client = client;
    }

    public void notify(ServerMessage msg) {
        System.out.println(SET_TEXT_COLOR_RED + msg.getServerMessage());

        if (msg.getGame() != null) {
            client.setGame(msg.getGame());
        }

//        printPrompt();
    }

//    private void printPrompt() {
//        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
//    }
}
