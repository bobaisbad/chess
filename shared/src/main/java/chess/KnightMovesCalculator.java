package chess;

import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {

    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Collection<ChessMove> calculator(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] moves = {
                {row + 2}, {col + 1},
                {row + 1}, {col + 2},
                {row - 1}, {col + 2},
                {row - 2}, {col + 1},
                {row - 2}, {col - 1},
                {row - 1}, {col - 2},
                {row + 1}, {col - 2},
                {row + 2}, {col - 1}
        };

        return super.calculator(moves, 16, 1);
    }
}
