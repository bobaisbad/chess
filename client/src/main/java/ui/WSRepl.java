package ui;

import websocket.NotificationHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class WSRepl implements NotificationHandler {
    public void notify(ServerMessage msg) {
        System.out.println(SET_TEXT_COLOR_RED + msg.getServerMessage());
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }
}
