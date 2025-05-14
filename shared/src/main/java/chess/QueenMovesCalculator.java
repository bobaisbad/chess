package chess;

import java.util.Collection;
import java.util.ArrayList;
// import java.util.function.Function;

public class QueenMovesCalculator {

    public Collection<ChessMove> queenCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        // Function<Integer, Integer>[][] funcs = new Function[][];

        int[][] nums = {
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

        for (int i = 0; i < 16; i += 2) {
            for (int j = 0; j < 7; j++) {
                if (nums[i][j] <= 8 && nums[i][j] >= 1 && nums[i + 1][j] <= 8 && nums[i + 1][j] >= 1) {
                    newPosition.setRow(nums[i][j]);
                    newPosition.setCol(nums[i + 1][j]);

                    if (board.getPiece(newPosition) == null) { // || board.getPiece(newPosition).getTeamColor() != color) {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(nums[i][j], nums[i + 1][j]), null));
                        // System.out.println("{" + nums[i][j] + ", " + nums[i + 1][j] + "}");
//                        if (board.getPiece(newPosition) != null) { // || board.getPiece(newPosition).getTeamColor() != color) {
//                            break;
//                        }
                    } else if (board.getPiece(newPosition).getTeamColor() != color) {
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(nums[i][j], nums[i + 1][j]), null));
                        // System.out.println("{" + nums[i][j] + ", " + nums[i + 1][j] + "}");
                        break;
                    } else {
                        break;
                    }
                }
            }
        }

        return movesArray;
    }
}
