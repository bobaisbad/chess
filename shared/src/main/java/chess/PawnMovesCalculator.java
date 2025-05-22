package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {
    private final ChessBoard board;

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
    }

    private ArrayList<ChessMove> movement(int start, int otherStart, String direction, ChessPosition newPosition,
                                          ChessPosition myPosition, ArrayList<ChessMove> movesArray) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        if (row == start) {
            for (int i = 1; i < 3; i++) {
                if (direction.equals("up")) {
                    newPosition.setRow(row + i);
                } else {
                    newPosition.setRow(row - i);
                }

                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null && direction.equals("up")) {
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row + i, col), null));
                } else if (newPiece == null && direction.equals("down")) {
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row - i, col), null));
                } else {
                    break;
                }
            }
        } else {
            if (direction.equals("up")) {
                newPosition.setRow(row + 1);
            } else {
                newPosition.setRow(row - 1);
            }
            ChessPiece newPiece = board.getPiece(newPosition);

            if (newPiece == null) {
                if (row == otherStart && direction.equals("up")) {
                    ChessPosition promo = new ChessPosition(row + 1, col);
                    getPromos(movesArray, myPosition, promo);
                } else if (row == otherStart && direction.equals("down")) {
                    ChessPosition promo = new ChessPosition(row - 1, col);
                    getPromos(movesArray, myPosition, promo);
                } else if (direction.equals("up")) {
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                } else if (direction.equals("down")) {
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
                }
            }
        }

        return movesArray;
    }

    private ArrayList<ChessMove> attacks(int otherStart, int[][] moves, ChessPosition newPosition,
                                         ChessPosition myPosition, ChessPiece piece, ArrayList<ChessMove> movesArray) {
        int row = myPosition.getRow();

        for (int i = 0; i < 2; i++) {
            if (moves[0][i] >= 1 && moves[0][i] <= 8 && moves[1][i] >= 1 && moves[1][i] <= 8) {
                newPosition.setRow(moves[0][i]);
                newPosition.setCol(moves[1][i]);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece != null && newPiece.getTeamColor() != piece.getTeamColor()) {
                    if (row == otherStart) {
                        ChessPosition promo = new ChessPosition(moves[0][i], moves[1][i]);
                        getPromos(movesArray, myPosition, promo);
                    } else {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(moves[0][i], moves[1][i]), null));
                    }
                }
            }
        }

        return movesArray;
    }

    public Collection<ChessMove> calculator(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            movesArray = movement(2, 7,"up", newPosition, myPosition, movesArray);

            int[][] moves = {
                    {row + 1, row + 1},
                    {col - 1, col + 1}
            };

            movesArray = attacks(7, moves, newPosition, myPosition, piece, movesArray);

        } else {
            movesArray = movement(7, 2,"down", newPosition, myPosition, movesArray);

            int[][] moves = {
                    {row - 1, row - 1},
                    {col - 1, col + 1}
            };

            movesArray = attacks(2, moves, newPosition, myPosition, piece, movesArray);
        }

        return movesArray;
    }

    private void getPromos(ArrayList<ChessMove> movesArray, ChessPosition myPosition, ChessPosition promo) {
        movesArray.add(new ChessMove(myPosition, promo, ChessPiece.PieceType.QUEEN));
        movesArray.add(new ChessMove(myPosition, promo, ChessPiece.PieceType.BISHOP));
        movesArray.add(new ChessMove(myPosition, promo, ChessPiece.PieceType.KNIGHT));
        movesArray.add(new ChessMove(myPosition, promo, ChessPiece.PieceType.ROOK));
    }
}
