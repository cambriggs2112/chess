package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    // -1 = Out of bounds
    //  0 = Same team piece present
    //  1 = Enemy team piece present
    //  2 = Empty space
    private int moveHereCheck(ChessBoard board, ChessPosition newPosition) {
        int row = newPosition.getRow();
        int col = newPosition.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return -1;
        }
        if (board.getPiece(newPosition) == null) {
            return 2;
        }
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            if (board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                return 0;
            }
            // enemy white
            return 1;
        }
        // piece white
        if (board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.WHITE) {
            return 0;
        }
        // enemy black
        return 1;
    }
    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> result = new HashSet<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        switch (type) {
            case KING: // Check if spaces have an enemy or are empty
                ChessPosition newPosition = new ChessPosition(row + 1, col - 1); // up and left
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col); // up
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col + 1); // up and right
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row, col - 1); // left
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row, col + 1); // right
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col - 1); // down and left
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col); // down
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col + 1); // down and right
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            case QUEEN:
                break;
            case BISHOP:
                break;
            case KNIGHT:
                break;
            case ROOK:
                break;
            case PAWN:
                break;
        }
        return result;
    }
}
