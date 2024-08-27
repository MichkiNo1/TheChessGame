package com.example.thechessgame.Pieces;

import com.example.thechessgame.BoardStateCallback;

public class Rook extends ChessPiece {

    public Rook(boolean isWhite, BoardStateCallback boardStateCallback) {
        super(isWhite, boardStateCallback);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Verify the rook moves in a straight line either horizontally or vertically
        boolean isStraightLineMove = fromX == toX || fromY == toY;

        if (!isStraightLineMove) {
            return false; // The move does not adhere to rook's movement rules
        }

        // Check if the path to the destination is clear of other pieces
        if (!isPathClear(fromX, fromY, toX, toY)) {
            return false; // The path is blocked
        }

        // If the destination square is occupied, it must be by an opponent's piece
        if (isSquareOccupied(toX, toY) && !isOpponentPiece(toX, toY)) {
            return false; // Can't capture your own piece
        }

        return true;
    }


    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck) {
        // For the rook, checking attack is the same as a regular move
        return isValidMove(fromX, fromY, toX, toY);
    }

}
