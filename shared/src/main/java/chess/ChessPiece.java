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
        ChessPosition newPosition = new ChessPosition(row, col);
        switch (type) {
            case KING: // Check if spaces have an enemy or are empty
                newPosition = new ChessPosition(row + 1, col - 1); // up and left
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
            case QUEEN, ROOK: // check straight lines
                newPosition = new ChessPosition(row + 1, col); // up
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row + 1, col);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row, col + 1); // right
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row, col + 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col); // down
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row - 1, col);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row, col - 1); // left
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row, col - 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                if (type == PieceType.ROOK) {
                    break; // Queen moves on to diagonals
                }
            case BISHOP: // check diagonals
                newPosition = new ChessPosition(row + 1, col + 1); // up and right
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row + 1, col + 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col + 1); // down and right
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row - 1, col + 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col - 1); // down and left
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row - 1, col - 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col - 1); // up and left
                while (moveHereCheck(board, newPosition) == 2) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                    newPosition = new ChessPosition(row + 1, col - 1);
                }
                if (moveHereCheck(board, newPosition) == 1) { // check for enemy at end
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            case KNIGHT: // L shapes
                newPosition = new ChessPosition(row + 2, col - 1); // up 2, left 1
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 2, col + 1); // up 2, right 1
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col - 2); // up 1, left 2
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col + 2); // up 1, right 2
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 1, col - 2); // down 1, left 2
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row + 1, col + 2); // down 1, right 2
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 2, col - 1); // down 2, left 1
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                newPosition = new ChessPosition(row - 2, col + 1); // down 2, right 1
                if (moveHereCheck(board, newPosition) >= 1) {
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
                break;
            case PAWN:
                if (pieceColor == ChessGame.TeamColor.WHITE) { // White moves up
                    // Basic forward motion
                    newPosition = new ChessPosition(row + 1, col);
                    if (moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    newPosition = new ChessPosition(row + 2, col);
                    if (row == 1 && moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // Diagonal enemy capture
                    newPosition = new ChessPosition(row + 1, col - 1);
                    if (moveHereCheck(board, newPosition) == 1) { // Space MUST have enemy
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    newPosition = new ChessPosition(row + 1, col + 1);
                    if (moveHereCheck(board, newPosition) == 1) { // Space MUST have enemy
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                } else { // Black moves down
                    // Basic forward motion
                    newPosition = new ChessPosition(row - 1, col);
                    if (moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    newPosition = new ChessPosition(row - 2, col);
                    if (row == 8 && moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    // Diagonal enemy capture
                    newPosition = new ChessPosition(row - 1, col - 1);
                    if (moveHereCheck(board, newPosition) == 1) { // Space MUST have enemy
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                    newPosition = new ChessPosition(row - 1, col + 1);
                    if (moveHereCheck(board, newPosition) == 1) { // Space MUST have enemy
                        result.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
                break;
        }
        return result;
    }
}
