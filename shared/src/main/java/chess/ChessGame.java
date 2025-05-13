package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();
    private ArrayList<ChessPiece> blackPieces = new ArrayList<>();
    private ArrayList<ChessPiece> whitePieces = new ArrayList<>();
    private final ChessPiece blackKing;
    private final ChessPiece whiteKing;

    public ChessGame() {
        board.resetBoard();
        ChessPiece piece;
        ChessPosition position = new ChessPosition(1, 5);
        blackKing = board.getPiece(position);
        position.setRow(8);
        whiteKing = board.getPiece(position);

        for (int i = 1; i < 9; i++) {
            position.setCol(i);
            for (int j = 1; j < 3; j++) {
                position.setRow(j);
                piece = board.getPiece(position);
                piece.setPosition(position);
                whitePieces.add(piece);
            }
            for (int l = 7; l < 9; l++) {
                position.setRow(l);
                piece = board.getPiece(position);
                piece.setPosition(position);
                blackPieces.add(piece);
            }
        }
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece != null) {
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

            for (ChessMove move : moves) {

            }

            return moves;
        } else {
            return null;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> enemyMoves;

        if (teamColor == TeamColor.WHITE) {
            for (ChessPiece enemy : blackPieces) {
                enemyMoves = enemy.pieceMoves(board, enemy.getPosition());

                for (ChessMove move : enemyMoves) {
                    if (move.getEndPosition() == whiteKing.getPosition()) {
                        return true;
                    }
                }
            }

        } else {
            for (ChessPiece enemy : whitePieces) {
                enemyMoves = enemy.pieceMoves(board, enemy.getPosition());

                for (ChessMove move : enemyMoves) {
                    if (move.getEndPosition() == blackKing.getPosition()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, board);
    }
}
