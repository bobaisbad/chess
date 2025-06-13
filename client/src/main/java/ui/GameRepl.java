package ui;

import chess.*;
import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class GameRepl implements NotificationHandler {
    private ChessGame game;
    private ChessClient client;
    private PrintStream out;

    public void run(ChessClient client, Scanner scanner) {
        this.client = client;

        var result = "";
        this.out = (out == null) ? new PrintStream(System.out, true, StandardCharsets.UTF_8) : out;

        while (!client.getQuit() && client.getGameStatus()) {
            if (client.getResigned()) {
                resigned(client, scanner, out);
                break;
            }

            printPrompt();
            String line = scanner.nextLine();
            client.setGame(game);
            result = client.gameEval(line);

            if (result.equals("resign") && !client.getResigned()) {
                System.out.print(SET_TEXT_COLOR_BLUE + "Are you sure you want to resign and end the game? (y/n)");
                printPrompt();
                line = scanner.nextLine();
                client.gameEval("resign " + line);
            } else if (result.equals("redraw")) {
                System.out.print("\n");
                if (client.getColor() == null || client.getColor() == ChessGame.TeamColor.WHITE) {
                    printBoardWhite(new ChessMove[0]);
                } else {
                    printBoardBlack(new ChessMove[0]);
                }
            } else {
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            }
        }
    }

    public void notify(ServerMessage msg) {
        if (msg.getGame() != null) {
            client.setGame(msg.getGame());
            this.game = msg.getGame();
        }

        if (msg.getServerMessage() != null) {
            System.out.print(SET_TEXT_COLOR_RED + msg.getServerMessage() + "\n");
            printPrompt();
        } else if (msg.getErrorMessage() != null) {
            System.out.print(SET_TEXT_COLOR_RED + msg.getErrorMessage() + "\n");
            printPrompt();
        }

        if (msg.getServerMessageType() == ServerMessage.ServerMessageType.NOTIFICATION) {
            if (msg.getGameOver()) {
                client.setResigned(true);
                game.setGameOver(true);
                if (client.getColor().equals(msg.getWinner())) {
                    System.out.print(SET_TEXT_COLOR_RED + "You won!" + "\n");
                    printPrompt();
                }
            }
        }
    }

    private void resigned(ChessClient client, Scanner scanner, PrintStream out) {
        var result = "";

        while (client.getGameStatus()) {
            printPrompt();
            String line = scanner.nextLine();
            result = client.resignedEval(line);

            if (result.equals("redraw")) {
                System.out.print("\n");
                printBoardWhite(new ChessMove[0]);
            } else {
                System.out.print(SET_TEXT_COLOR_BLUE + result + "\n");
            }
        }
    }

    private void printPrompt() {
        if (game != null) {
             if(!client.getResigned() && !game.getGameOver()) {
                 if (client.getColor() == game.getTeamTurn()) {
                     System.out.print("\n" + RESET_TEXT_COLOR + "[YOUR_TURN] >>> ");
                 } else {
                     System.out.print("\n" + RESET_TEXT_COLOR + "[" +
                                      ((client.getColor() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE) +
                                      "_TURN] >>> ");
                 }
             } else if (client.getColor() == null && client.getResigned() && !game.getGameOver()) {
                 System.out.print("\n" + RESET_TEXT_COLOR + "[OBSERVER] >>> ");
             } else if (client.getResigned() && game.getGameOver()) {
                 System.out.print("\n" + RESET_TEXT_COLOR + "[GAME_OVER] >>> ");
             } else {
                 System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
             }
        } else {
            System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
        }
    }

    public void printBoardBlack(ChessMove[] moves) {
        String[] letters = {" h\u2003", " g\u2003", " f\u2003", " e\u2003", " d\u2003", " c\u2003", " b\u2003", " a\u2003"};
        drawLetters(out, letters);
        char[] ascii = {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        int[] numbers = {1, 2, 3 , 4 , 5, 6, 7, 8};
        drawRows(out, numbers, ascii, moves);
        drawLetters(out, letters);
    }

    public void printBoardWhite(ChessMove[] moves) {
        String[] letters = {" a\u2003", " b\u2003", " c\u2003", " d\u2003", " e\u2003", " f\u2003", " g\u2003", " h\u2003"};
        drawLetters(out, letters);
        char[] ascii = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] numbers = {8, 7, 6, 5, 4, 3, 2, 1};
        drawRows(out, numbers, ascii, moves);
        drawLetters(out, letters);
    }

    private void drawLetters(PrintStream out, String[] letters) {
        setGray(out);
        out.print(EMPTY);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print(SET_TEXT_BOLD);
        for (int i = 0; i < 8; i++) {
            out.print(letters[i]);
        }
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(EMPTY);
        setDefault(out);
        out.println();
    }

    private void drawRows(PrintStream out, int[] numbers, char[] letters, ChessMove[] moves) {
        ChessBoard board = game.getBoard();
        ChessPosition position = new ChessPosition(1, 1);

        for (int i = 0; i < 8; i++) { // Rows
            setGray(out);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_TEXT_BOLD);
            out.print("\u2003" + numbers[i] + " ");
            out.print(RESET_TEXT_BOLD_FAINT);
            for (int j = 0; j < 8; j++) { // Columns
                if (j % 2 == 0 && i % 2 == 0) {
                    setTan(out);
                } else if (j % 2 == 1 && i % 2 == 1) {
                    setTan(out);
                } else {
                    setBrown(out);
                }

                setMoves(i, j, moves, numbers, letters);

                position.setCol(letters[j] - 96);
                position.setRow(numbers[i]);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    out.print(EMPTY);
                } else {
                    out.print(SET_TEXT_COLOR_BLACK);
                    String type = getPieceType(piece);
                    out.print(type);
                }
            }
            setGray(out);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_TEXT_BOLD);
            out.print(" " + numbers[i] + "\u2003");
            out.print(RESET_TEXT_BOLD_FAINT);
            setDefault(out);
            out.println();
        }
    }

    private void setMoves(int i, int j, ChessMove[] moves, int[] numbers, char[] letters) {
        for (ChessMove move : moves) {
            if (move.getEndPosition().getRow() == numbers[i] && move.getEndPosition().getColumn() == letters[j] - 96) {

                if (j % 2 == 0 && i % 2 == 0) {
                    setGreen(out);
                } else if (j % 2 == 1 && i % 2 == 1) {
                    setGreen(out);
                } else {
                    setDarkGreen(out);
                }
            } else if (move.getStartPosition().getRow() == numbers[i] && move.getStartPosition().getColumn() == letters[j] - 96) {
                setBlue(out);
            }
        }
    }

    private void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private void setBrown(PrintStream out) {
        out.print(SET_BG_COLOR_BROWN);
        out.print(SET_TEXT_COLOR_BROWN);
    }

    private void setTan(PrintStream out) {
        out.print(SET_BG_COLOR_TAN);
        out.print(SET_TEXT_COLOR_TAN);
    }

    private void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_GREEN);
    }

    private void setDarkGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_DARK_GREEN);
    }

    private void setBlue(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_BLUE);
    }

    private void setDefault(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private String getPieceType(ChessPiece piece) {
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
        } else {
            return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
        }
    }
}
