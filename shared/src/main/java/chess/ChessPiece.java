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

    private boolean canMoveHere(ChessBoard board, ChessPosition newPosition) {
        int row = newPosition.getRow();
        int col = newPosition.getColumn();
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            return false;
        }
        if (board.getPiece(newPosition) == null) {
            return true;
        }
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            return board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.WHITE;
        }
        return board.getPiece(newPosition).getTeamColor() == ChessGame.TeamColor.BLACK;
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
            case KING:
                ChessPosition newPosition = new ChessPosition(row + 1, col); // up
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row + 1, col + 1); // up right
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row, col + 1); // right
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row - 1, col + 1); // down right
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row - 1, col); // down
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row - 1, col - 1); // down left
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row, col - 1); // left
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
                }
                newPosition = new ChessPosition(row + 1, col - 1); // up left
                if (canMoveHere(board, newPosition)) {
                    result.add(new ChessMove(myPosition, newPosition, PieceType.KING));
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
