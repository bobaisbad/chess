package ui;

import exceptions.ParentException;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class PreRepl {
    private final PostRepl post = new PostRepl();

    public void run(ChessClient client) {
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.preEval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);

            if (client.getLoginStatus()) {
                System.out.print("\n");
                post.run(client, scanner);
            }
        }
        System.out.println();
    }

    public void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + "[LOGGED_OUT] >>> ");
    }
}
