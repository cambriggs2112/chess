package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private boolean whiteKingNeverMoved;
    private boolean whiteLeftRookNeverMoved;
    private boolean whiteRightRookNeverMoved;
    private boolean blackKingNeverMoved;
    private boolean blackLeftRookNeverMoved;
    private boolean blackRightRookNeverMoved;
    private TeamColor team;
    private ChessBoard board;

    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.whiteKingNeverMoved = true;
        this.whiteLeftRookNeverMoved = true;
        this.whiteRightRookNeverMoved = true;
        this.blackKingNeverMoved = true;
        this.blackLeftRookNeverMoved = true;
        this.blackRightRookNeverMoved = true;
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

    private boolean testForNoCheck(ChessMove move) {
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece startPiece = board.getPiece(startPosition);
        ChessPiece endPiece = board.getPiece(endPosition);
        board.addPiece(endPosition, startPiece);
        board.addPiece(startPosition, null);
        if (isInCheck(startPiece.getTeamColor())) {
            board.addPiece(endPosition, endPiece);
            board.addPiece(startPosition, startPiece);
            return false;
        }
        board.addPiece(endPosition, endPiece);
        board.addPiece(startPosition, startPiece);
        return true;
    }
    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null) {
            return null;
        } else {
            ArrayList<ChessMove> result = new ArrayList<ChessMove>();
            ChessPiece startPiece = board.getPiece(startPosition);
            for (ChessMove move : startPiece.pieceMoves(board, startPosition)) {
                if (testForNoCheck(move)) {
                    result.add(move);
                }
            }
            // Add castling moves (King/rook never moved, fully clear, king is never in check)
            // White
            if (startPiece.getTeamColor() == TeamColor.WHITE
                    && startPiece.getPieceType() == ChessPiece.PieceType.KING
                    && whiteKingNeverMoved
                    && !isInCheck(TeamColor.WHITE)) {
                // Left
                if (board.getPiece(new ChessPosition(1, 2)) == null
                        && board.getPiece(new ChessPosition(1, 3)) == null
                        && board.getPiece(new ChessPosition(1, 4)) == null
                        && whiteLeftRookNeverMoved
                        && testForNoCheck(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 4), null))
                        && testForNoCheck(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null))) {
                    result.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 3), null));
                }
                // Right
                if (board.getPiece(new ChessPosition(1, 6)) == null
                        && board.getPiece(new ChessPosition(1, 7)) == null
                        && whiteRightRookNeverMoved
                        && testForNoCheck(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 6), null))
                        && testForNoCheck(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null))) {
                    result.add(new ChessMove(new ChessPosition(1, 5), new ChessPosition(1, 7), null));
                }
            }
            // Black
            if (startPiece.getTeamColor() == TeamColor.BLACK
                    && startPiece.getPieceType() == ChessPiece.PieceType.KING
                    && blackKingNeverMoved
                    && !isInCheck(TeamColor.BLACK)) {
                // Left
                if (board.getPiece(new ChessPosition(8, 2)) == null
                        && board.getPiece(new ChessPosition(8, 3)) == null
                        && board.getPiece(new ChessPosition(8, 4)) == null
                        && blackLeftRookNeverMoved
                        && testForNoCheck(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 4), null))
                        && testForNoCheck(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null))) {
                    result.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 3), null));
                }
                // Right
                if (board.getPiece(new ChessPosition(8, 6)) == null
                        && board.getPiece(new ChessPosition(8, 7)) == null
                        && blackRightRookNeverMoved
                        && testForNoCheck(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 6), null))
                        && testForNoCheck(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null))) {
                    result.add(new ChessMove(new ChessPosition(8, 5), new ChessPosition(8, 7), null));
                }
            }
            // Add en passant moves (Other team moved pawn twice)
            return result;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = board.getPiece(move.getStartPosition());
        if (pieceToMove == null) {
            throw new InvalidMoveException("There is no piece here.");
        }
        if (pieceToMove.getTeamColor() != team) {
            throw new InvalidMoveException("This is the opposite team.");
        }
        if (validMoves(move.getStartPosition()) == null) {
            throw new InvalidMoveException("This piece has no valid moves.");
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException("This move cannot be made with this piece.");
        }
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(team, move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), pieceToMove);
        }
        board.addPiece(move.getStartPosition(), null);
        if (board.getPiece(new ChessPosition(1, 5)) == null || board.getPiece(new ChessPosition(1, 5)).getTeamColor() != TeamColor.WHITE || board.getPiece(new ChessPosition(1, 5)).getPieceType() != ChessPiece.PieceType.KING) {
            whiteKingNeverMoved = false;
        }
        if (board.getPiece(new ChessPosition(1, 1)) == null || board.getPiece(new ChessPosition(1, 1)).getTeamColor() != TeamColor.WHITE || board.getPiece(new ChessPosition(1, 1)).getPieceType() != ChessPiece.PieceType.ROOK) {
            whiteLeftRookNeverMoved = false;
        }
        if (board.getPiece(new ChessPosition(1, 8)) == null || board.getPiece(new ChessPosition(1, 8)).getTeamColor() != TeamColor.WHITE || board.getPiece(new ChessPosition(1, 8)).getPieceType() != ChessPiece.PieceType.ROOK) {
            whiteRightRookNeverMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 5)) == null || board.getPiece(new ChessPosition(8, 5)).getTeamColor() != TeamColor.BLACK || board.getPiece(new ChessPosition(8, 5)).getPieceType() != ChessPiece.PieceType.KING) {
            blackKingNeverMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 1)) == null || board.getPiece(new ChessPosition(8, 1)).getTeamColor() != TeamColor.BLACK || board.getPiece(new ChessPosition(8, 1)).getPieceType() != ChessPiece.PieceType.ROOK) {
            blackLeftRookNeverMoved = false;
        }
        if (board.getPiece(new ChessPosition(8, 8)) == null || board.getPiece(new ChessPosition(8, 8)).getTeamColor() != TeamColor.BLACK || board.getPiece(new ChessPosition(8, 8)).getPieceType() != ChessPiece.PieceType.ROOK) {
            blackRightRookNeverMoved = false;
        }
        if (team == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
        // If move is castling move by King, also move Rook
        if (pieceToMove.getPieceType() == ChessPiece.PieceType.KING) {
            if (pieceToMove.getTeamColor() == TeamColor.WHITE) {
                // Left
                if (move.getStartPosition().getRow() == 1
                        && move.getStartPosition().getColumn() == 5
                        && move.getEndPosition().getRow() == 1
                        && move.getEndPosition().getColumn() == 3) {
                    ChessPiece leftRook = board.getPiece(new ChessPosition(1, 1));
                    board.addPiece(new ChessPosition(1, 4), leftRook);
                    board.addPiece(new ChessPosition(1, 1), null);
                }
                // Right
                if (move.getStartPosition().getRow() == 1
                        && move.getStartPosition().getColumn() == 5
                        && move.getEndPosition().getRow() == 1
                        && move.getEndPosition().getColumn() == 7) {
                    ChessPiece rightRook = board.getPiece(new ChessPosition(1, 8));
                    board.addPiece(new ChessPosition(1, 6), rightRook);
                    board.addPiece(new ChessPosition(1, 8), null);
                }
            }
            if (pieceToMove.getTeamColor() == TeamColor.BLACK) {
                // Left
                if (move.getStartPosition().getRow() == 8
                        && move.getStartPosition().getColumn() == 5
                        && move.getEndPosition().getRow() == 8
                        && move.getEndPosition().getColumn() == 3) {
                    ChessPiece leftRook = board.getPiece(new ChessPosition(8, 1));
                    board.addPiece(new ChessPosition(8, 4), leftRook);
                    board.addPiece(new ChessPosition(8, 1), null);
                }
                // Right
                if (move.getStartPosition().getRow() == 8
                        && move.getStartPosition().getColumn() == 5
                        && move.getEndPosition().getRow() == 8
                        && move.getEndPosition().getColumn() == 7) {
                    ChessPiece rightRook = board.getPiece(new ChessPosition(8, 8));
                    board.addPiece(new ChessPosition(8, 6), rightRook);
                    board.addPiece(new ChessPosition(8, 8), null);
                }
            }
        }
        // If move is en passant move by Pawn, capture other Pawn
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = null;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    kingPos = pos;
                }
            }
        }
        if (kingPos == null) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    for (ChessMove move : piece.pieceMoves(board, pos)) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
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
        if (!isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece startPiece = board.getPiece(pos);
                if (startPiece != null && startPiece.getTeamColor() == teamColor) {
                    for (ChessMove move : startPiece.pieceMoves(board, pos)) {
                        if (testForNoCheck(move)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() == teamColor && !validMoves(pos).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
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
