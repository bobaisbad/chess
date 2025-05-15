package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {
    private final ChessBoard board;
    private final ChessPosition myPosition;
    private final ChessPiece piece;

    public PieceMovesCalculator (ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.piece = board.getPiece(myPosition);
    }

    public Collection<ChessMove> moveCalculator() { // (ChessBoard board, ChessPosition myPosition) {
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            KingMovesCalculator calc = new KingMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            QueenMovesCalculator calc = new QueenMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            BishopMovesCalculator calc = new BishopMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            KnightMovesCalculator calc = new KnightMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        } else if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            RookMovesCalculator calc = new RookMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        } else {
            PawnMovesCalculator calc = new PawnMovesCalculator(board, myPosition);
            return calc.calculator(myPosition);
        }
    }

    public Collection<ChessMove> calculator(int[][] moves, int r, int c) {
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(1, 1);

        for (int i = 0; i < r; i += 2) {
            for (int j = 0; j < c; j++) {
                if (moves[i][j] >= 1 && moves[i][j] <= 8 && moves[i + 1][j] >= 1 && moves[i + 1][j] <= 8) {
                    newPosition.setRow(moves[i][j]);
                    newPosition.setCol(moves[i + 1][j]);
                    ChessPiece newPiece = board.getPiece(newPosition);

                    if (newPiece == null || newPiece.getTeamColor() != piece.getTeamColor()) {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(moves[i][j], moves[i + 1][j]), null));

                        if (newPiece != null && newPiece.getTeamColor() != piece.getTeamColor()) {
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
        }

        return movesArray;
    }
}
