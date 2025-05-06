package chess;

import java.util.Collection;
import java.util.ArrayList;

public class BishopMovesCalculator {
//    private ChessPiece piece;
//    private ChessGame.TeamColor color;

    public Collection<ChessMove> bishopCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
//        System.out.println("Creating vars...");
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);
        row += 1;
        col += 1;
        newPosition.setRow(row);
        newPosition.setCol(col);
//        System.out.println("Starting to search...");

        while (row <= 8 && col <= 8) { // && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color)) {
//            System.out.println("Piece type: " + board.getPiece(newPosition));
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
//                System.out.println("Type: " + board.getPiece(newPosition).getPieceType());
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row += 1;
            col += 1;
            newPosition.setRow(row);
            newPosition.setCol(col);
        }

        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() + 1;
        newPosition.setRow(row);
        newPosition.setCol(col);

        while (row >= 1 && col <= 8) { // && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color)) {
//            System.out.println("Piece type: " + board.getPiece(newPosition));
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
//                System.out.println("Type: " + board.getPiece(newPosition).getPieceType());
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row -= 1;
            col += 1;
            newPosition.setRow(row);
            newPosition.setCol(col);
        }

        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() - 1;
        newPosition.setRow(row);
        newPosition.setCol(col);

        while (row >= 1 && col >= 1) { // && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color)) {
//            System.out.println("Piece type: " + board.getPiece(newPosition));
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
//                System.out.println("Type: " + board.getPiece(newPosition).getPieceType());
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row -= 1;
            col -= 1;
            newPosition.setRow(row);
            newPosition.setCol(col);
        }

        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() - 1;
        newPosition.setRow(row);
        newPosition.setCol(col);

        while (row <= 8 && col >= 1) { // && (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color)) {
//            System.out.println("Piece type: " + board.getPiece(newPosition));
            if (board.getPiece(newPosition) == null) {
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
            } else if (board.getPiece(newPosition).getTeamColor() != color) {
//                System.out.println("Type: " + board.getPiece(newPosition).getPieceType());
                System.out.println("Endpoint: {" + row + ", " + col + "}");
                movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                break;
            } else {
                break;
            }

            row += 1;
            col -= 1;
            newPosition.setRow(row);
            newPosition.setCol(col);
        }

        return movesArray;
    }


}
