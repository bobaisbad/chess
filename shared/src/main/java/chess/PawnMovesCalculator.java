package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalculator {

    public Collection<ChessMove> pawnCalculator(ChessPiece piece, ChessGame.TeamColor color, ChessPosition myPosition, ChessBoard board) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ArrayList<ChessMove> movesArray = new ArrayList<>();
        ChessPosition newPosition = new ChessPosition(row, col);

        if (color == ChessGame.TeamColor.BLACK) {
            if (row == 7) {
                while (row > 5) {
                    row -= 1;
                    newPosition.setRow(row);

                    if (board.getPiece(newPosition) == null) {
                        System.out.println("Endpoint: {" + row + ", " + col + "}");
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    } else {
                        break;
                    }
                }
            } else if (row > 1) {
                row -= 1;
                newPosition.setRow(row);

                if (board.getPiece(newPosition) == null) {
                    System.out.println("Endpoint: {" + row + ", " + col + "}");
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }

            row = myPosition.getRow();

            int[][] nums = {
                    {row - 1, row - 1},
                    {col - 1, col + 1}
            };

            for (int i = 0; i < 2; i++) {
                if (nums[0][i] >= 1 && nums[1][i] <= 8 && nums[1][i] >= 1) {
                    newPosition.setRow(nums[0][i]);
                    newPosition.setCol(nums[1][i]);

                    if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != color) {
                        System.out.println("Endpoint: {" + nums[0][i] + ", " + nums[1][i] + "}");
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(nums[0][i], nums[1][i]), null));
                    }
                }
            }

        } else if (color == ChessGame.TeamColor.WHITE) {
            if (row == 2) {
                while (row < 4) {
                    row += 1;
                    newPosition.setRow(row);

                    if (board.getPiece(newPosition) == null) {
                        System.out.println("Endpoint: {" + row + ", " + col + "}");
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                    } else {
                        break;
                    }
                }
            } else if (row < 8) {
                row += 1;
                newPosition.setRow(row);

                if (board.getPiece(newPosition) == null) {
                    System.out.println("Endpoint: {" + row + ", " + col + "}");
                    movesArray.add(new ChessMove(myPosition, new ChessPosition(row, col), null));
                }
            }

            row = myPosition.getRow();

            int[][] nums = {
                    {row + 1, row + 1},
                    {col - 1, col + 1}
            };

            for (int i = 0; i < 2; i++) {
                if (nums[0][i] <= 8 && nums[1][i] <= 8 && nums[1][i] >= 1) {
                    newPosition.setRow(nums[0][i]);
                    newPosition.setCol(nums[1][i]);

                    if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != color) {
                        System.out.println("Endpoint: {" + nums[0][i] + ", " + nums[1][i] + "}");
                        movesArray.add(new ChessMove(myPosition, new ChessPosition(nums[0][i], nums[1][i]), null));
                    }
                }
            }
        }

        return movesArray;
    }
}
