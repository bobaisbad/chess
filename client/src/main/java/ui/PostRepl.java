package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PostRepl {
    private final GameRepl game = new GameRepl();

    public void run(ChessClient client, Scanner scanner) {
        var result = "";

        while (client.getLoginStatus() && !client.getQuit()) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.postEval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);
            System.out.print("\n");

            if (client.getGameStatus()) {
                game.run(client, scanner);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> ");
    }

    public GameRepl getGameRepl() {
        return game;
    }
}
