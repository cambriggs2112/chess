package chess;

import java.util.Collection;
import java.util.ArrayList;
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

    /**
     * Helper method to help determine whether piece can move to a given position
     *
     * @return number corresponding to conditions at new position:
     *                -1 = Out of bounds
     *                0 = Occupied by same team
     *                1 = Occupied by other team
     *                2 = Unoccupied
     */
    private int moveHereCheck(ChessBoard board, ChessPosition newPosition) {
        int row = newPosition.getRow();
        int col = newPosition.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return -1;
        }
        if (board.getPiece(newPosition) == null) {
            return 2;
        }
        if (board.getPiece(newPosition).getTeamColor() == pieceColor) {
            return 0;
        }
        return 1;
    }

    /**
     * Helper method that calculates all the positions a KING can move to
     */
    private ArrayList<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
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
        return result;
    }

    /**
     * Helper method that calculates all the positions a QUEEN can move to
     */
    private ArrayList<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = bishopMoves(board, myPosition);
        result.addAll(rookMoves(board, myPosition));
        return result;
    }

    /**
     * Helper method that calculates all the positions a BISHOP can move to
     */
    private ArrayList<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        // up and right
        int rowTemp = row + 1;
        int colTemp = col + 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp++;
            colTemp++;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // down and right
        rowTemp = row - 1;
        colTemp = col + 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp--;
            colTemp++;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // down and left
        rowTemp = row - 1;
        colTemp = col - 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp--;
            colTemp--;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // up and left
        rowTemp = row + 1;
        colTemp = col - 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp++;
            colTemp--;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        return result;
    }

    /**
     * Helper method that calculates all the positions a KNIGHT can move to
     */
    private ArrayList<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition newPosition = new ChessPosition(row + 2, col - 1); // up 2, left 1
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
        newPosition = new ChessPosition(row - 1, col + 2); // down 1, right 2
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
        return result;
    }

    /**
     * Helper method that calculates all the positions a ROOK can move to
     */
    private ArrayList<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        // up
        int rowTemp = row + 1;
        int colTemp = col;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp++;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // right
        rowTemp = row;
        colTemp = col + 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            colTemp++;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // down
        rowTemp = row - 1;
        colTemp = col;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            rowTemp--;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        // left
        rowTemp = row;
        colTemp = col - 1;
        while (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 2) {
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
            colTemp--;
        }
        if (moveHereCheck(board, new ChessPosition(rowTemp, colTemp)) == 1) { // check for enemy at end
            result.add(new ChessMove(myPosition, new ChessPosition(rowTemp, colTemp), null));
        }
        return result;
    }

    /**
     * Helper method that adds moves to a PAWN moves list
     */
    private void addPawnMoves(ArrayList<ChessMove> list, ChessBoard board,
                              ChessPosition myPosition, ChessPosition newPosition,
                              int row, int rowCompare, int destCompare) {
        if (moveHereCheck(board, newPosition) == destCompare) {
            if (row == rowCompare) { // Promotion
                list.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
                list.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
                list.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
                list.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            } else {
                list.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    /**
     * Helper method that calculates all the positions a PAWN can move to
     */
    private ArrayList<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> result = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        ChessPosition newPosition;
        if (pieceColor == ChessGame.TeamColor.WHITE) { // White moves up
            // Basic forward motion
            newPosition = new ChessPosition(row + 1, col);
            addPawnMoves(result, board, myPosition, newPosition, row, 7, 2); // Space MUST be empty
            if (moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                newPosition = new ChessPosition(row + 2, col);
                if (row == 2 && moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            // Diagonal enemy capture
            newPosition = new ChessPosition(row + 1, col - 1);
            addPawnMoves(result, board, myPosition, newPosition, row, 7, 1); // Space MUST have enemy
            newPosition = new ChessPosition(row + 1, col + 1);
            addPawnMoves(result, board, myPosition, newPosition, row, 7, 1); // Space MUST have enemy
        } else { // Black moves down
            // Basic forward motion
            newPosition = new ChessPosition(row - 1, col);
            addPawnMoves(result, board, myPosition, newPosition, row, 2, 2); // Space MUST be empty
            if (moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                newPosition = new ChessPosition(row - 2, col);
                if (row == 7 && moveHereCheck(board, newPosition) == 2) { // Space MUST be empty
                    result.add(new ChessMove(myPosition, newPosition, null));
                }
            }
            // Diagonal enemy capture
            newPosition = new ChessPosition(row - 1, col - 1);
            addPawnMoves(result, board, myPosition, newPosition, row, 2, 1); // Space MUST have enemy
            newPosition = new ChessPosition(row - 1, col + 1);
            addPawnMoves(result, board, myPosition, newPosition, row, 2, 1); // Space MUST have enemy
        }
        return result;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case KING: return kingMoves(board, myPosition);
            case QUEEN: return queenMoves(board, myPosition);
            case BISHOP: return bishopMoves(board, myPosition);
            case KNIGHT: return knightMoves(board, myPosition);
            case ROOK: return rookMoves(board, myPosition);
            case PAWN: return pawnMoves(board, myPosition);
            default: return null;
        }
    }
}
