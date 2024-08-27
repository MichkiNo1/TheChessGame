package com.example.thechessgame.Pieces;


import com.example.thechessgame.BoardStateCallback;

public class Bishop extends ChessPiece {

    public Bishop(boolean isWhite, BoardStateCallback boardStateCallback) {
        super(isWhite, boardStateCallback);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Bishops move diagonally, so the absolute difference between x and y coordinates must be equal.
        if (Math.abs(fromX - toX) != Math.abs(fromY - toY)) {
            return false; // Not a diagonal move.
        }

        // Ensure the path between the start and end points is clear, except possibly the end point itself.
        if (!isPathClear(fromX, fromY, toX, toY)) {
            return false; // Path is not clear.
        }

        // If the destination square is occupied, it must be by an opponent's piece to be a valid capture.
        if (isSquareOccupied(toX, toY)) {
            return isOpponentPiece(toX, toY);
        }

        // Move is diagonal and the path is clear.
        return true;
    }

    // Overloaded method for scenarios like checking if moving to a square would put the bishop in check
    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck) {
        // For the Bishop, this method isn't different because its movement and capture are the same.
        // However, we include it for consistency with the interface.
        return isValidMove(fromX, fromY, toX, toY);
    }
}
