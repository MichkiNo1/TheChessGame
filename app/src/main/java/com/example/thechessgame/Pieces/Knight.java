package com.example.thechessgame.Pieces;

import com.example.thechessgame.BoardStateCallback;


public class Knight extends ChessPiece {

    public Knight(boolean isWhite, BoardStateCallback boardStateCallback) {
        super(isWhite, boardStateCallback);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Calculate the change in x and y to determine if the move is in an "L" shape
        int deltaX = Math.abs(fromX - toX);
        int deltaY = Math.abs(fromY - toY);

        // A valid knight move must either move 2 squares in one direction and 1 square in another (the "L" shape)
        boolean isValidLMove = (deltaX == 2 && deltaY == 1) || (deltaX == 1 && deltaY == 2);

        if (!isValidLMove) {
            return false; // Not a valid "L" shaped move
        }

        // Knights can jump over pieces, so no need to check if the path is clear. Just check destination occupancy.
        if (isSquareOccupied(toX, toY)) {
            // If the destination square is occupied, it must be by an opponent's piece to be a valid move.
            return isOpponentPiece(toX, toY);
        }

        // If the square is not occupied, or is occupied by an opponent's piece, the move is valid.
        return true;
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck) {
        // For the Knight, checking for attack is the same as a regular move check since it can jump pieces.
        return isValidMove(fromX, fromY, toX, toY);
    }
}
