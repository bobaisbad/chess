import Exceptions.ParentException;
import chess.*;
import server.Server;

public class Main {
    public static void main(String[] args) {
        try {
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);

            var server = new Server("memory");
            server.run(8080);
        } catch (ParentException ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}