package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreRepl {
    private final PostRepl post = new PostRepl();

    public void run(ChessClient client) {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!client.getQuit()) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.preEval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);
            System.out.print("\n");

            if (client.getLoginStatus()) {
                post.run(client, scanner);
            }
        }
//        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> ");
    }

    public GameRepl getGameRepl() {
        return post.getGameRepl();
    }
}
