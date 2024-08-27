package com.example.thechessgame.Pieces;

import com.example.thechessgame.BoardStateCallback;

/**
 * Abstract base class for all chess pieces.
 */
public abstract class ChessPiece {
    protected boolean isWhite;
    protected boolean hasMoved = false;
    protected BoardStateCallback boardStateCallback;

    /**
     * Constructs a ChessPiece instance.
     *
     * @param isWhite            Indicates whether the piece is white.
     * @param boardStateCallback A callback interface to access the game board state.
     */
    public ChessPiece(boolean isWhite, BoardStateCallback boardStateCallback) {
        this.isWhite = isWhite;
        this.boardStateCallback = boardStateCallback;
    }

    /**
     * Sets the moved status of the piece.
     *
     * @param hasMoved Indicates whether the piece has moved.
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Checks if the piece has moved.
     *
     * @return True if the piece has moved, otherwise false.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Checks if the piece is white.
     *
     * @return True if the piece is white, otherwise false.
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Checks if a square is occupied.
     *
     * @param x The x-coordinate of the square.
     * @param y The y-coordinate of the square.
     * @return True if the square is occupied, otherwise false.
     */
    protected boolean isSquareOccupied(int x, int y) {
        return boardStateCallback.isSquareOccupied(x, y);
    }

    /**
     * Checks if the path between two squares is clear.
     *
     * @param fromX The x-coordinate of the starting square.
     * @param fromY The y-coordinate of the starting square.
     * @param toX   The x-coordinate of the ending square.
     * @param toY   The y-coordinate of the ending square.
     * @return True if the path is clear, otherwise false.
     */
    protected boolean isPathClear(int fromX, int fromY, int toX, int toY) {
        return boardStateCallback.isPathClear(fromX, fromY, toX, toY);
    }

    /**
     * Checks if a square is under attack by the opponent.
     *
     * @param x       The x-coordinate of the square.
     * @param y       The y-coordinate of the square.
     * @param isWhite The color of the attacking pieces.
     * @return True if the square is under attack, otherwise false.
     */
    protected boolean isSquareUnderAttack(int x, int y, boolean isWhite) {
        return boardStateCallback.isSquareUnderAttack(x, y, isWhite);
    }
    protected boolean isOpponentPiece(int x, int y) {
        ChessPiece piece = boardStateCallback.getChessPieceAt(x, y);
        return piece != null && piece.isWhite() != this.isWhite();
    }


    /**
     * Determines whether a move from one square to another is valid.
     *
     * @param fromX The x-coordinate of the starting square.
     * @param fromY The y-coordinate of the starting square.
     * @param toX   The x-coordinate of the ending square.
     * @param toY   The y-coordinate of the ending square.
     * @return True if the move is valid, otherwise false.
     */
    public abstract boolean isValidMove(int fromX, int fromY, int toX, int toY);
    public abstract boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck);


}
