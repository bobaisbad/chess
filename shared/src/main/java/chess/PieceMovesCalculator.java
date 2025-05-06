package chess;

import java.util.Collection;

public class PieceMovesCalculator {
    private ChessPiece piece;
    private ChessPiece.PieceType type;
    private ChessGame.TeamColor color;

    public Collection<ChessMove> calculator(ChessBoard board, ChessPosition myPosition) {
        this.piece = board.getPiece(myPosition);
        this.type = piece.getPieceType();
        this.color = piece.getTeamColor();

//        if (type == ChessPiece.PieceType.BISHOP) {
        BishopMovesCalculator calc = new BishopMovesCalculator();
//        System.out.println("Pushing into Bishop Calc...");
        return calc.bishopCalculator(piece, color, myPosition, board);
//        } else if (type == ChessPiece.PieceType.QUEEN) {
//            //
//        } else if (type == ChessPiece.PieceType.ROOK) {
//            //
//        } else if (type == ChessPiece.PieceType.KNIGHT) {
//            //
//        } else if (type == ChessPiece.PieceType.BISHOP) {
//            return BishopMovesCalculator();
//        } else {
//            //
//        }
    }
}
