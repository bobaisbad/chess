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
    // private ChessBoard simBoard;
    // private ArrayList<ChessPiece> blackPieces = new ArrayList<>();
    // private ArrayList<ChessPiece> whitePieces = new ArrayList<>();
    // private ArrayList<ChessPiece> simBlackPieces = new ArrayList<>();
    // private ArrayList<ChessPiece> simWhitePieces = new ArrayList<>();
    private ChessPosition blackKingPosition;
    private ChessPosition whiteKingPosition;

    public ChessGame() {
        board.resetBoard();
        // System.out.print(board.getPiece(new ChessPosition(1, 1)).getPieceType());
//        board.resetBoard();
//        ChessPiece piece;
        blackKingPosition = new ChessPosition(8, 5);
        whiteKingPosition = new ChessPosition(1, 5);
//
//        for (int i = 1; i < 9; i++) {
//            position.setCol(i);
//            for (int j = 1; j < 3; j++) {
//                position.setRow(j);
//                piece = board.getPiece(position);
//                piece.setPosition(position);
//            }
//            for (int l = 7; l < 9; l++) {
//                position.setRow(l);
//                piece = board.getPiece(position);
//                piece.setPosition(position);
//            }
//        }
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
        // throw new RuntimeException("Not implemented");
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

        for (ChessMove move : moves) {
            ChessBoard copy = board.clone();

            board.setPiece(piece, move.getEndPosition());
            board.setPiece(null, move.getStartPosition());

            if (isInCheck(piece.getTeamColor())) {
                moves.remove(move);
            }

            board = copy;
        }

        return moves;
//        ChessPiece piece = board.getPiece(startPosition);
//
//        if (piece != null) {
//            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);

//            for (ChessMove move : moves) {
                // simBoard = board.clone();
                // ChessPiece[][] pieces = simBoard.getPieces();
                // ChessPiece attacked = simBoard.getPiece(move.getEndPosition());

//                if (attacked != null) {
//                    ChessPosition off = new ChessPosition(0, 0);
//                    attacked.setPosition(off);
//                    if (attacked.getTeamColor() == TeamColor.WHITE) {
//                        // simWhitePieces = (ArrayList<ChessPiece>) whitePieces.clone();
//                        // simWhitePieces = clonePieceArray(whitePieces, attacked);
//                        // simWhitePieces.remove(attacked);
//                    } else {
//                        // simBlackPieces = (ArrayList<ChessPiece>) blackPieces.clone();
//                        // simBlackPieces = clonePieceArray(blackPieces, attacked);
//                        // simBlackPieces.remove(attacked);
//                    }
//                }
//
//                simBoard.setPiece(piece, move.getEndPosition());
//                simBoard.setPiece(null, move.getStartPosition());
//                piece.setPosition(move.getEndPosition());
//
//                if (isInCheck(piece.getTeamColor())) {
//                    moves.remove(move);
//                }
//
//                if (attacked != null) {
//                    attacked.setPosition(move.getEndPosition());
//                }
//            }
//
//            piece.setPosition(startPosition);
//
//            return moves;
//        } else {
//            return null;
//        }
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

        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        // for (ChessMove movement : moves) {
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

            if (board.getPiece(move.getEndPosition()).getPieceType() == ChessPiece.PieceType.KING) {
                if (team == TeamColor.WHITE) {
                    whiteKingPosition = move.getEndPosition();
                } else {
                    blackKingPosition = move.getEndPosition();
                }
            }

            if (team == TeamColor.WHITE) {
                setTeamTurn(TeamColor.BLACK);
            } else {
                setTeamTurn(TeamColor.WHITE);
            }
        } else {
            // }
            throw new InvalidMoveException();
        }
        // throw new RuntimeException("Not implemented");
//        Collection<ChessMove> moves = validMoves(move.getStartPosition());
//
//        if (moves.contains(move)) {
//            //
//        } else {
//            throw new InvalidMoveException();
//        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // throw new RuntimeException("Not implemented");
        ChessPosition position = new ChessPosition(1, 1);

        for (int i = 1; i < 9; i++) {
            position.setRow(i);
            for (int j = 1; j < 9; j++) {
                position.setCol(j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, position);

                    for (ChessMove move : moves) {
                        if (teamColor == TeamColor.WHITE && move.getEndPosition() == whiteKingPosition) {
                            return true;
                        } else if (teamColor == TeamColor.BLACK && move.getEndPosition() == blackKingPosition) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;

//        Collection<ChessMove> enemyMoves;
//        ChessPosition off = new ChessPosition(0, 0);
//        simBoard = board.clone();
//        ChessPiece[][] pieces = simBoard.getPieces();
//        ChessPiece enemy;
//
////        System.out.println("Comparing clones...");
////        System.out.println(simBoard.getPieces() == board.getPieces());
////        System.out.println(simBoard == board);
//
//        System.out.print("White Pieces: {");
//        for (int i = 0; i < 15; i++) {
//            System.out.print(board.getPieces()[1][1] + ", ");
//        }
//        System.out.print(board.getPieces()[1][15] + "}\n\n");
//
//        System.out.print("White Sim Pieces: {");
//        for (int i = 0; i < 15; i++) {
//            System.out.print(simBoard.getPieces()[1][1] + ", ");
//        }
//        System.out.print(simBoard.getPieces()[1][15] + "}\n\n");
//
//
//        if (teamColor == TeamColor.WHITE) {
//            for (int i = 0; i < 16; i++) {
//            // for (ChessPiece[][] enemy : pieces) {
//                // if (simBoard.getPiece(enemy.getPosition()) != null) {
//                    // enemyMoves = enemy.pieceMoves(simBoard, enemy.getPosition());
//                enemy = pieces[1][i];
//                if (enemy != null && enemy.getPosition() != off) {
//                    enemyMoves = enemy.pieceMoves(simBoard, enemy.getPosition());
//
//                    for (ChessMove move : enemyMoves) {
//                        if (move.getEndPosition() == whiteKing.getPosition()) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        } else {
//            for (int i = 0; i < 16; i++) {
//                // for (ChessPiece[][] enemy : pieces) {
//                // if (simBoard.getPiece(enemy.getPosition()) != null) {
//                // enemyMoves = enemy.pieceMoves(simBoard, enemy.getPosition());
//                enemy = pieces[0][i];
//                if (enemy != null && enemy.getPosition() != off) {
//                    enemyMoves = enemy.pieceMoves(simBoard, enemy.getPosition());
//
//                    for (ChessMove move : enemyMoves) {
//                        if (move.getEndPosition() == blackKing.getPosition()) {
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//
//        return false;
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

//    public ArrayList<ChessPiece> clonePieceArray(ArrayList<ChessPiece> pieces, ChessPiece removed) {
//        ArrayList<ChessPiece> clonedPieces = new ArrayList<>();
//
//        for (ChessPiece piece : pieces) {
//            if (!piece.equals(removed)) {
//                clonedPieces.add(piece);
//            }
//        }
//
//        return clonedPieces;
//    }
}
