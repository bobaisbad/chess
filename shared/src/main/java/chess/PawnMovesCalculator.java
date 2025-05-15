package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator {
    private final ChessBoard board;

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
        this.board = board;
    }

    public Collection<ChessMove> calculator(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPiece piece = board.getPiece(myPosition);
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                for (int i = 1; i < 3; i++) {
                    newPosition.setRow(row + i);
                    ChessPiece newPiece = board.getPiece(newPosition);

                    if (newPiece == null) {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row + i, col), null));
                    } else {
                        break;
                    }
                }
            } else {
                newPosition.setRow(row + 1);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    if (row == 7) {
                        ChessPosition promo = new ChessPosition(row + 1, col);
                        getPromos(movesArray, myPosition, promo);
                    } else {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row + 1, col), null));
                    }
                }
            }

            int[][] moves = {
                    {row + 1, row + 1},
                    {col - 1, col + 1}
            };

            for (int i = 0; i < 2; i++) {
                if (moves[0][i] >= 1 && moves[0][i] <= 8 && moves[1][i] >= 1 && moves[1][i] <= 8) {
                    newPosition.setRow(moves[0][i]);
                    newPosition.setCol(moves[1][i]);
                    ChessPiece newPiece = board.getPiece(newPosition);

                    if (newPiece != null && newPiece.getTeamColor() != piece.getTeamColor()) {
                        if (row == 7) {
                            ChessPosition promo = new ChessPosition(moves[0][i], moves[1][i]);
                            getPromos(movesArray, myPosition, promo);
                        } else {
                            movesArray.add(new ChessMove(myPosition, new ChessPosition(moves[0][i], moves[1][i]), null));
                        }
                    }
                }
            }
        } else {
            if (row == 7) {
                for (int i = 1; i < 3; i++) {
                    newPosition.setRow(row - i);
                    ChessPiece newPiece = board.getPiece(newPosition);

                    if (newPiece == null) {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row - i, col), null));
                    } else {
                        break;
                    }
                }
            } else {
                newPosition.setRow(row - 1);
                ChessPiece newPiece = board.getPiece(newPosition);

                if (newPiece == null) {
                    if (row == 2) {
                        ChessPosition promo = new ChessPosition(row - 1, col);
                        getPromos(movesArray, myPosition, promo);
                    } else {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row - 1, col), null));
                    }
                }
            }

            int[][] moves = {
                    {row - 1, row - 1},
                    {col - 1, col + 1}
            };

            for (int i = 0; i < 2; i++) {
                if (moves[0][i] >= 1 && moves[0][i] <= 8 && moves[1][i] >= 1 && moves[1][i] <= 8) {
                    newPosition.setRow(moves[0][i]);
                    newPosition.setCol(moves[1][i]);
                    ChessPiece newPiece = board.getPiece(newPosition);

                    if (newPiece != null && newPiece.getTeamColor() != piece.getTeamColor()) {
                        if (row == 2) {
                            ChessPosition promo = new ChessPosition(moves[0][i], moves[1][i]);
                            getPromos(movesArray, myPosition, promo);
                        } else {
                            movesArray.add(new ChessMove(myPosition, new ChessPosition(moves[0][i], moves[1][i]), null));
                        }
                    }
                }
            }
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
