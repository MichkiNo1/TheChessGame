package com.example.thechessgame;

import android.graphics.Point;
import com.example.thechessgame.Pieces.ChessPiece;

public class Move {
    Point from;
    Point to;
    ChessPiece movedPiece;
    ChessPiece capturedPiece;
    boolean wasFirstMove;
    boolean wasCastlingMove;
    Point castlingRookFrom;
    Point castlingRookTo;

    // Constructor for regular moves
    public Move(Point from, Point to, ChessPiece movedPiece, ChessPiece capturedPiece, boolean wasFirstMove) {
        this(from, to, movedPiece, capturedPiece, wasFirstMove, false, null, null);
    }

    // Constructor for castling moves
    public Move(Point from, Point to, ChessPiece movedPiece, ChessPiece capturedPiece,
                boolean wasFirstMove, boolean wasCastlingMove, Point castlingRookFrom, Point castlingRookTo) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.wasFirstMove = wasFirstMove;
        this.wasCastlingMove = wasCastlingMove;
        this.castlingRookFrom = castlingRookFrom;
        this.castlingRookTo = castlingRookTo;
    }

    // Getters (and setters if necessary) for each field
    public Point getFrom() {
        return from;
    }

    public Point getTo() {
        return to;
    }

    public ChessPiece getMovedPiece() {
        return movedPiece;
    }

    public ChessPiece getCapturedPiece() {
        return capturedPiece;
    }

    public boolean wasFirstMove() {
        return wasFirstMove;
    }

    public boolean wasCastlingMove() {
        return wasCastlingMove;
    }

    public Point getCastlingRookFrom() {
        return castlingRookFrom;
    }

    public Point getCastlingRookTo() {
        return castlingRookTo;
    }

    // You might add additional methods here, such as:
    // - A method to revert the move (if that logic doesn't live elsewhere)
    // - A method to describe the move in human-readable form (for logging or debugging)
}
