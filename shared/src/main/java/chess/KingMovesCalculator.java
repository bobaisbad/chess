package chess;

import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    public KingMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Collection<ChessMove> calculator(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] moves = {
                {row + 1}, {col},
                {row + 1}, {col + 1},
                {row}, {col + 1},
                {row - 1}, {col + 1},
                {row - 1}, {col},
                {row - 1}, {col - 1},
                {row}, {col - 1},
                {row + 1}, {col - 1}
        };

        return super.calculator(moves, 16, 1);
    }
}

