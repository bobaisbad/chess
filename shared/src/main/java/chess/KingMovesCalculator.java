package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalculator {

    public Collection<ChessMove> kingCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);
        row += 1;
        newPosition.setRow(row);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        col += 1;
        newPosition.setCol(col);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        row -= 1;
        newPosition.setRow(row);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        row -= 1;
        newPosition.setRow(row);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        col -= 1;
        newPosition.setCol(col);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        col -= 1;
        newPosition.setCol(col);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        row += 1;
        newPosition.setRow(row);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        row += 1;
        newPosition.setRow(row);
        System.out.println("{" + row + ", " + col + "}");

        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
            movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
        }

        return movesArray;
    }

//    private void checkSquare(ChessPiece piece, ChessGame.TeamColor color, ArrayList<ChessMove> array, ) {
//        if (piece == null || piece.getTeamColor() != color) {
//            array.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
//        }
//    }
}
