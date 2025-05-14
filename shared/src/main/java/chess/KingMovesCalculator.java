package chess;

import java.util.Collection;
import java.util.ArrayList;

public class KingMovesCalculator {

    public Collection<ChessMove> kingCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        int[][] nums = {
                {row + 1, row + 1, row, row - 1, row - 1, row - 1, row, row + 1},
                {col, col + 1, col + 1, col + 1, col, col - 1, col - 1, col - 1}
        };

        for (int i = 0; i < 8; i++) {
            if (nums[0][i] <= 8 && nums[0][i] >= 1 && nums[1][i] <= 8 && nums[1][i] >= 1) {
                newPosition.setRow(nums[0][i]);
                newPosition.setCol(nums[1][i]);

                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != color) {
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(nums[0][i], nums[1][i]), null));
                    // System.out.println("{" + nums[0][i] + ", " + nums[1][i] + "}");
                }
            }
        }

        return movesArray;
    }
}
