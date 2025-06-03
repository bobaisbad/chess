package ui;

import exceptions.ParentException;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreRepl {
    GameRepl game = new GameRepl();
    PostRepl post = new PostRepl();

    public void run(ChessClient client) {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.preEval(line);
            System.out.print(result);

            if (client.getLoginStatus()) {
                post.run(client);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }
}
