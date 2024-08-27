package com.example.thechessgame.Pieces;

import android.util.Log;
import com.example.thechessgame.BoardStateCallback;

public class King extends ChessPiece {

    public King(boolean isWhite, BoardStateCallback boardStateCallback) {
        super(isWhite, boardStateCallback);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // This method redirects to the overloaded method with attackCheck set to false by default
        return isValidMove(fromX, fromY, toX, toY, false);
    }

    @Override
    public boolean isValidMove(int fromX, int fromY, int toX, int toY, boolean isAttackCheck) {
        // Standard king movement: one square in any direction
        if (Math.abs(fromX - toX) <= 1 && Math.abs(fromY - toY) <= 1) {
            // Ensure the destination square is either empty or occupied by an opponent's piece
            if (!isSquareOccupied(toX, toY) || (isSquareOccupied(toX, toY) && isOpponentPiece(toX, toY))) {
                // Additionally, ensure the move does not place the king into check, unless checking for attacks
                if (!isAttackCheck && isSquareUnderAttack(toX, toY, !isWhite())) {
                    return false; // Moving here would place the king in check
                }
                return true; // Valid move
            }
        }

        // Castling logic, if applicable
        if (!hasMoved && !isAttackCheck && Math.abs(toX - fromX) == 2 && fromY == toY) {
            // Check for castling conditions here
            return canCastle(fromX, fromY, toX, toY);
        }

        return false; // Not a valid move
    }
    private boolean canCastle(int fromX, int fromY, int toX, int toY) {
        // Simplified castling check; expand as needed for your game's rules

        // Castling is only allowed if the king has not moved, moves two squares to the left or right,
        // and the path between the king and the target square is clear and not under attack.
        if (fromY != toY || Math.abs(toX - fromX) != 2 || boardStateCallback.isSquareUnderAttack(fromX, fromY, isWhite())) {
            return false;
        }

        int direction = toX > fromX ? 1 : -1;
        for (int x = fromX + direction; x != toX; x += direction) {
            if (boardStateCallback.isSquareOccupied(x, fromY) || boardStateCallback.isSquareUnderAttack(x, fromY, isWhite())) {
                return false;
            }
        }

        // Assuming the rook's position is fixed at either 0 or 7 for x, depending on the castling side
        int rookX = direction > 0 ? 7 : 0;
        ChessPiece rook = boardStateCallback.getChessPieceAt(rookX, fromY);
        Log.d("CastlingCheck", "Rook at (" + rookX + ", " + fromY + ") hasMoved: " + (rook != null && rook.hasMoved()));
        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        return true;
    }


}
