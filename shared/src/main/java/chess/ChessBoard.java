package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPiece[][] pieces;

    public ChessBoard() {
        this.pieces = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        pieces = new ChessPiece[][]{
                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK)},
                {new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN)},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN)},
                {new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT),
                        new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK)}};
    }
}
