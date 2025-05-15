package chess;

import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {

    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    public Collection<ChessMove> calculator(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        int[][] moves = {
                {row + 1, row + 2, row + 3, row + 4, row + 5, row + 6, row + 7},
                {col, col, col, col, col, col, col},
                {row + 1, row + 2, row + 3, row + 4, row + 5, row + 6, row + 7},
                {col + 1, col + 2, col + 3, col + 4, col + 5, col + 6, col + 7},
                {row, row, row, row, row, row, row},
                {col + 1, col + 2, col + 3, col + 4, col + 5, col + 6, col + 7},
                {row - 1, row - 2, row - 3, row - 4, row - 5, row - 6, row - 7},
                {col + 1, col + 2, col + 3, col + 4, col + 5, col + 6, col + 7},
                {row - 1, row - 2, row - 3, row - 4, row - 5, row - 6, row - 7},
                {col, col, col, col, col, col, col},
                {row - 1, row - 2, row - 3, row - 4, row - 5, row - 6, row - 7},
                {col - 1, col - 2, col - 3, col - 4, col - 5, col - 6, col - 7},
                {row, row, row, row, row, row, row},
                {col - 1, col - 2, col - 3, col - 4, col - 5, col - 6, col - 7},
                {row + 1, row + 2, row + 3, row + 4, row + 5, row + 6, row + 7},
                {col - 1, col - 2, col - 3, col - 4, col - 5, col - 6, col - 7},
        };

        return super.calculator(moves, 16, 7);
    }
}

