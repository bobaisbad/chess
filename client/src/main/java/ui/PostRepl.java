package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PostRepl {
    private final GameRepl game = new GameRepl();

    public void run(ChessClient client, Scanner scanner) {
        var result = "";

        while (!result.equals("quit") && client.getLoginStatus()) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.postEval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);

            if (client.getGameStatus()) {
                System.out.print("\n");
                game.run(client, scanner);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_IN] >>> ");
    }
}
