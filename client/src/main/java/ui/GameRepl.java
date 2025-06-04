package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_ROOK;

public class GameRepl {
    private ChessGame game;

    public void run(ChessClient client, Scanner scanner) {
        var result = "";
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        this.game = client.getGame();

        while (!result.equals("quit") && !client.getQuit()) {
            if (client.getColor().equals("black")) {
                printBoardBlack(out);
            } else {
                printBoardWhite(out);
            }
            System.out.print("\n");
            printPrompt();
            String line = scanner.nextLine();
            result = client.gameEval(line);
            System.out.print(SET_TEXT_COLOR_BLUE + result);
            System.out.print("\n");
        }
//        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }

    private void printBoardBlack(PrintStream out) {
        String[] letters = {" h\u2003", " g\u2003", " f\u2003", " e\u2003", " d\u2003", " c\u2003", " b\u2003", " a\u2003"};
        drawLetters(out, letters);
        char[] ascii = {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};
        int[] numbers = {1, 2, 3 , 4 , 5, 6, 7, 8};
//        String[][] pieces = {
//                {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
//                {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
//                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//                {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//                {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
//                {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
//        };
        drawRows(out, numbers, ascii);
        drawLetters(out, letters);
    }

    private void printBoardWhite(PrintStream out) {
        String[] letters = {" a\u2003", " b\u2003", " c\u2003", " d\u2003", " e\u2003", " f\u2003", " g\u2003", " h\u2003"};
        drawLetters(out, letters);
        char[] ascii = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        int[] numbers = {8, 7, 6, 5, 4, 3, 2, 1};
        drawRows(out, numbers, ascii);
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

    private void drawRows(PrintStream out, int[] numbers, char[] letters) {
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

    private void setDefault(PrintStream out) {
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

    private String getPieceType(ChessPiece piece) {
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                return WHITE_PAWN;
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                return WHITE_ROOK;
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                return WHITE_KNIGHT;
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return WHITE_BISHOP;
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                return WHITE_QUEEN;
            } else {
                return WHITE_KING;
            }
        } else {
            if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
                return BLACK_PAWN;
            } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
                return BLACK_ROOK;
            } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
                return BLACK_KNIGHT;
            } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
                return BLACK_BISHOP;
            } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
                return BLACK_QUEEN;
            } else {
                return BLACK_KING;
            }
        }
    }
}
