package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator {

    public Collection<ChessMove> rookCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        col += 1;
        newPosition.setCol(col);

        while (col <= 8) {
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            col += 1;
            newPosition.setCol(col);
        }

        col = myPosition.getColumn() - 1;
        newPosition.setCol(col);

        while (col >= 1) {
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            col -= 1;
            newPosition.setCol(col);
        }

        col = myPosition.getColumn();
        newPosition.setCol(col);
        row -= 1;
        newPosition.setRow(row);

        while (row >= 1) {
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row -= 1;
            newPosition.setRow(row);
        }

        row = myPosition.getRow() + 1;
        newPosition.setRow(row);

        while (row <= 8) {
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row += 1;
            newPosition.setRow(row);
        }

        return movesArray;
    }
}
