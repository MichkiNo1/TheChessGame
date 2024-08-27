package com.example.thechessgame.Pieces;


import com.example.thechessgame.BoardStateCallback;

public class Pawn extends ChessPiece {

    public Pawn(boolean isWhite, BoardStateCallback boardStateCallback) {
        super(isWhite, boardStateCallback);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Determine pawn direction based on color
        int direction = isWhite ? -1 : 1;
        int startRow = isWhite ? 6 : 1; // Starting row for white and black pawns

        // Forward move by 1
        if (fromX == toX && toY - fromY == direction && !isSquareOccupied(toX, toY)) {
            return true;
        }

        // Initial move by 2
        if (fromX == toX && fromY == startRow && toY - fromY == 2 * direction &&
                !isSquareOccupied(toX, toY) && !isSquareOccupied(toX, fromY + direction)) {
            return true;
        }

        // Diagonal capture
        if (Math.abs(toX - fromX) == 1 && toY - fromY == direction && isOpponentPiece(toX, toY)) {
            return true;
        }

        // TODO: Implement en passant capture logic here

        // TODO: Implement promotion logic here

        return false; // If none of the above conditions are met, it's an invalid move
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck) {
        // Attack check for Pawn, used for checking if a move is a valid capture
        if (isAttackCheck) {
            int direction = isWhite ? -1 : 1;
            return Math.abs(toX - fromX) == 1 && toY - fromY == direction && isOpponentPiece(toX, toY);
        } else {
            return isValidMove(fromX, fromY, toX, toY);
        }
    }

}
