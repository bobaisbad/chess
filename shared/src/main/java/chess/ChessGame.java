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
    private final ChessPosition blackKingPosition;
    private final ChessPosition whiteKingPosition;

    public ChessGame() {
        board.resetBoard();
        blackKingPosition = new ChessPosition(8, 5);
        whiteKingPosition = new ChessPosition(1, 5);
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

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        ArrayList<ChessMove> filtered = new ArrayList<>();

        for (ChessMove move : moves) {
            ChessBoard copy = board.clone();

            board.setPiece(piece, move.getEndPosition());
            board.setPiece(null, move.getStartPosition());

            updateKingsPosition(piece, move.getEndPosition());

            if (!isInCheck(piece.getTeamColor())) {
                filtered.add(move);
            }

            updateKingsPosition(piece, move.getStartPosition());

            board = copy;
        }

        return filtered;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null ||
            board.getPiece(move.getStartPosition()).getTeamColor() != team) {
            throw new InvalidMoveException();
        }

        if (isInCheck(team)) {
            isInCheckmate(team);
        } else {
            isInStalemate(team);
        }

        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        if (moves.contains(move)) {
            ChessPiece piece;

            if (move.getPromotionPiece() == ChessPiece.PieceType.QUEEN) {
                piece = new ChessPiece(team, ChessPiece.PieceType.QUEEN);
            } else if (move.getPromotionPiece() == ChessPiece.PieceType.ROOK) {
                piece = new ChessPiece(team, ChessPiece.PieceType.ROOK);
            } else if (move.getPromotionPiece() == ChessPiece.PieceType.KNIGHT) {
                piece = new ChessPiece(team, ChessPiece.PieceType.KNIGHT);
            } else if (move.getPromotionPiece() == ChessPiece.PieceType.BISHOP) {
                piece = new ChessPiece(team, ChessPiece.PieceType.BISHOP);
            } else {
                piece = board.getPiece(move.getStartPosition());
            }

            board.setPiece(piece, move.getEndPosition());
            board.setPiece(null, move.getStartPosition());

            updateKingsPosition(piece, move.getEndPosition());

            if (team == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition position = new ChessPosition(1, 1);

        updateKings(position);

        for (int i = 1; i < 9; i++) {
            position.setRow(i);
            for (int j = 1; j < 9; j++) {
                position.setCol(j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);

                    boolean result = checkMoves(teamColor, moves);

                    if (result) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean checkMoves(TeamColor teamColor, Collection<ChessMove> moves) {
        for (ChessMove move : moves) {
            if (teamColor == TeamColor.WHITE && move.getEndPosition().equals(whiteKingPosition)) {
                return true;
            } else if (teamColor == TeamColor.BLACK && move.getEndPosition().equals(blackKingPosition)) {
                return true;
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
        ChessPosition position = new ChessPosition(1, 1);

        updateKings(position);

        for (int i = 1; i < 9; i++) {
            position.setRow(i);
            for (int j = 1; j < 9; j++) {
                position.setCol(j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);

                    if (!moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return isInCheckmate(teamColor);
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

    private void updateKings(ChessPosition position) {
        for (int i = 1; i < 9; i++) {
            position.setRow(i);
            for (int j = 1; j < 9; j++) {
                position.setCol(j);
                ChessPiece piece = board.getPiece(position);
                updateKingsPosition(piece, position);
            }
        }
    }

    private void updateKingsPosition(ChessPiece piece, ChessPosition position) {
        if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING) {
            if (piece.getTeamColor() == TeamColor.WHITE) {
                whiteKingPosition.setRow(position.getRow());
                whiteKingPosition.setCol(position.getColumn());
            } else {
                blackKingPosition.setRow(position.getRow());
                blackKingPosition.setCol(position.getColumn());
            }
        }
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
