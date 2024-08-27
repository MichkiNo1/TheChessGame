package com.example.thechessgame;

import com.example.thechessgame.Pieces.ChessPiece;
public interface BoardStateCallback {
    boolean isSquareOccupied(int x, int y);
    boolean isPathClear(int fromX, int fromY, int toX, int toY);
    boolean isSquareUnderAttack(int x, int y, boolean isWhite);
    ChessPiece getChessPieceAt(int x, int y); // Retrieve a chess piece at the given coordinates
}

