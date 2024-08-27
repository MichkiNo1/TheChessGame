package com.example.thechessgame;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.thechessgame.Pieces.*;

import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BoardStateCallback {
    private Map<Point, ChessPiece> chessBoard = new HashMap<>();
    private ImageView[][] boardViews = new ImageView[8][8];
    private Point selectedPiecePosition = null;
    private GridLayout chessBoardLayout;
    private int squareSize;
    private final int columnCount = 8;
    private boolean isWhiteTurn = true;
    private Button undoMoveButton;
    GameDatabaseHelper dbHelper;
    private TextView playerOneTimerTextView, playerTwoTimerTextView;
    private CountDownTimer playerOneTimer, playerTwoTimer;
    private boolean isPlayerOneTimerRunning;
    private boolean isPlayerTwoTimerRunning;
    private Button rotateBoardButton;
    String playerOneName, playerTwoName;
    AlarmManager alarmManager;
    private boolean isBoardRotated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Call this method to create the notification channel
        createNotificationChannel();
        // Initialize AlarmManager
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);



        dbHelper = new GameDatabaseHelper(this);



        Intent intent = getIntent();
        playerOneName = intent.getStringExtra("playerOneName");
        playerOneName = playerOneName != null ? playerOneName : "Player 1";

        playerTwoName = intent.getStringExtra("playerTwoName");
        playerTwoName = playerTwoName != null ? playerTwoName : "Player 2";
        boolean playerOneIsWhite = intent.getBooleanExtra("playerOneIsWhite", true);

        // Find the TextViews
        TextView playerOneNameTextView = findViewById(R.id.playerOneNameTextView);
        TextView playerTwoNameTextView = findViewById(R.id.playerTwoNameTextView);

        // Update the TextViews with player names and colors
        if (playerOneIsWhite) {
            playerOneNameTextView.setText(playerOneName + " (Black)");
            playerTwoNameTextView.setText(playerTwoName + " (White)");
        } else {
            playerOneNameTextView.setText(playerOneName + " (White)");
            playerTwoNameTextView.setText(playerTwoName + " (Black)");
        }

        chessBoardLayout = findViewById(R.id.chessBoardLayout);
        initializeBoard();
        setupPieces();
        Button btnBackToOpening = findViewById(R.id.back_to_opening);
        btnBackToOpening.setOnClickListener(v -> navigateBackToOpening());

        undoMoveButton = findViewById(R.id.undo_move); // Assigning to the field, not a local variable
        // Correctly setting the OnClickListener to the undo button
        undoMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoLastMove();
            }
        });
        // Initialize TextViews for timers
        playerOneTimerTextView = findViewById(R.id.playerOneTimerTextView);
        playerTwoTimerTextView = findViewById(R.id.playerTwoTimerTextView);

        // Initialize and start the timer for player one as an example
        startPlayerOneTimer();


        rotateBoardButton = findViewById(R.id.rotateBoardButton);
        rotateBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateBoard();
            }
        });

    }
    private void rotateBoard() {
        isBoardRotated = !isBoardRotated; // Toggle the board rotation state

        // Rotate the board view itself if needed
        chessBoardLayout.setRotation(isBoardRotated ? 180 : 0);

        // Update all piece images according to the new rotation state
        updatePieceImagesForRotation();
    }


    // Utility method to convert dp to pixels
    private void initializeBoard() {
        int totalSquares = 64;
        squareSize = getResources().getDisplayMetrics().widthPixels / columnCount; // Using columnCount which should be defined as 8
        chessBoardLayout.removeAllViews();

        for (int i = 0; i < totalSquares; i++) {
            final int x = i % columnCount;
            final int y = i / columnCount;
            ImageView square = new ImageView(this);
            square.setScaleType(ImageView.ScaleType.FIT_CENTER);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = squareSize;
            layoutParams.height = squareSize;
            square.setLayoutParams(layoutParams);

            // Use ContextCompat.getColor to fetch the color
            int backgroundColor = (x + y) % 2 == 0 ? ContextCompat.getColor(this, R.color.dark_square) : ContextCompat.getColor(this, R.color.light_square);
            square.setBackgroundColor(backgroundColor);

            square.setOnClickListener(v -> handleSquareClick(x, y)); // Use lambda expression directly
            chessBoardLayout.addView(square);
            boardViews[y][x] = square; // Store the ImageView in an array for quick access
        }
    }
    private void setupPieces() {

        // Place Black Pieces
        chessBoard.put(new Point(0, 0), new Rook(false, this));
        chessBoard.put(new Point(1, 0), new Knight(false, this));
        chessBoard.put(new Point(2, 0), new Bishop(false, this));
        chessBoard.put(new Point(3, 0), new Queen(false, this));
        chessBoard.put(new Point(4, 0), new King(false, this));
        chessBoard.put(new Point(5, 0), new Bishop(false, this));
        chessBoard.put(new Point(6, 0), new Knight(false, this));
        chessBoard.put(new Point(7, 0), new Rook(false, this));
        for (int i = 0; i < 8; i++) {
            chessBoard.put(new Point(i, 1), new Pawn(false, this));
        }

        // Place White Pieces
        chessBoard.put(new Point(0, 7), new Rook(true, this));
        chessBoard.put(new Point(1, 7), new Knight(true, this));
        chessBoard.put(new Point(2, 7), new Bishop(true, this));
        chessBoard.put(new Point(3, 7), new Queen(true, this));
        chessBoard.put(new Point(4, 7), new King(true, this));
        chessBoard.put(new Point(5, 7), new Bishop(true, this));
        chessBoard.put(new Point(6, 7), new Knight(true, this));
        chessBoard.put(new Point(7, 7), new Rook(true, this));
        for (int i = 0; i < 8; i++) {
            chessBoard.put(new Point(i, 6), new Pawn(true, this));
        }

        updateBoardView(); // Call this method to refresh the board UI with the initial setup
    }
    private void handleSquareClick(int x, int y) {
        Point clickedPoint = new Point(x, y);
        ChessPiece clickedPiece = chessBoard.get(clickedPoint);

        resetHighlight(); // Reset highlights at the start of the method

        if (clickedPiece != null && clickedPiece.isWhite() == isWhiteTurn) {
            if (selectedPiecePosition == null || !selectedPiecePosition.equals(clickedPoint)) {
                // Highlight the selected piece
                selectedPiecePosition = clickedPoint;
                highlightPossibleMoves(clickedPiece, x, y);
            } else {
                selectedPiecePosition = null; // Deselect if the same piece is clicked again
            }
        } else if (selectedPiecePosition != null) {
            ChessPiece selectedPiece = chessBoard.get(selectedPiecePosition);

            // Special handling for castling
            if (selectedPiece instanceof King && (Math.abs(x - selectedPiecePosition.x) == 2)) {
                // Attempt to castle
                if (attemptCastle(selectedPiecePosition, clickedPoint)) {
                    togglePlayerTurn();
                    resetHighlight(); // Reset any square highlights after castling
                    handleMove(selectedPiecePosition, clickedPoint);
                    selectedPiecePosition = null; // Deselect piece after successful castling
                    updateBoardView();
                    return; // Early return as castling was successful
                }
            }

            // Attempt to make a move
            if (validateAndMakeMove(selectedPiecePosition, clickedPoint)) {
                togglePlayerTurn();
                resetHighlight(); // Reset any square highlights after moving
                selectedPiecePosition = null; // Deselect the piece after attempting a move
            } else {
                // If the move or castling is not valid, reset the highlight and selection
                resetHighlight();
                selectedPiecePosition = null;
            }
        }

        updateBoardView();
        updateBoardViewRespectingRotation();
    }
    private void updateBoardViewRespectingRotation() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ImageView squareImageView = boardViews[y][x];
                ChessPiece piece = chessBoard.get(new Point(x, y));
                if (piece != null) {
                    // Use the rotation-aware method to get the correct drawable ID
                    int drawableId = isBoardRotated ? getRotatedDrawableIdForPiece(piece) : getDrawableIdForPiece(piece);
                    squareImageView.setImageResource(drawableId);
                } else {
                    squareImageView.setImageDrawable(null);
                }
            }
        }
    }

    private void highlightPossibleMoves(ChessPiece piece, int x, int y) {
        resetHighlight(); // Clear existing highlights

        // Highlight normal moves
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (piece.isValidMove(x, y, col, row) && isMoveLegalUnderCheck(new Point(x, y), new Point(col, row), piece.isWhite())) {
                    // Log for debugging
                    Log.d("Highlight", "Highlighting (" + col + ", " + row + ")");
                    highlightSquare(row, col, android.R.color.holo_blue_light);
                }
            }
        }

        // Castling checks
        if (piece instanceof King && !piece.hasMoved()) {
            if (isCastlingPossible((King) piece, x, y, true)) highlightCastlingMove(y, 6);
            if (isCastlingPossible((King) piece, x, y, false)) highlightCastlingMove(y, 2);
        }
    }

    private void highlightSquare(int row, int col, int colorResource) {
        ImageView squareImageView = boardViews[row][col];
        squareImageView.setBackgroundColor(ContextCompat.getColor(this, colorResource));
    }

    private void highlightCastlingMove(int y, int targetX) {
        ImageView squareImageView = boardViews[y][targetX];
        squareImageView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_light)); // Highlight color for castling move
    }

    private boolean isCastlingPossible(King king, int kingX, int kingY, boolean kingSide) {
        // Assuming your isPathClear method checks if the squares between two points are unoccupied
        int direction = kingSide ? 1 : -1;
        int rookX = kingSide ? 7 : 0;
        int targetX = kingSide ? 6 : 2; // Target position for the king after castling

        // Early return if any square the king would move through is under attack
        for (int x = kingX + direction; kingSide ? x <= targetX : x >= targetX; x += direction) {
            if (isSquareUnderAttack(x, kingY, !king.isWhite())) {
                return false;
            }
        }

        // Check if path between king and rook is clear and if the rook has moved
        ChessPiece rook = chessBoard.get(new Point(rookX, kingY));
        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        return isPathClear(kingX, kingY, rookX, kingY); // Check if the path between the king and the rook is clear
    }

    private void togglePlayerTurn() {
        isWhiteTurn = !isWhiteTurn; // Toggle the turn
        if (isWhiteTurn) {
            if (playerTwoTimer != null) {
                playerTwoTimer.cancel();
                isPlayerTwoTimerRunning = false;
            }
            startPlayerOneTimer();
        } else {
            if (playerOneTimer != null) {
                playerOneTimer.cancel();
                isPlayerOneTimerRunning = false;
            }
            startPlayerTwoTimer();
        }
    }

    private boolean validateAndMakeMove(Point from, Point to) {
        ChessPiece piece = chessBoard.get(from);
        if (piece != null && piece.isValidMove(from.x, from.y, to.x, to.y) && isMoveLegalUnderCheck(from, to, piece.isWhite())) {
            makeMove(from, to); // Physically move the piece on the board
            return true; // Move was successful
        }
        return false; // Move was invalid
    }

    // This method checks if a move is legal given the current check status.
    private boolean isMoveLegalUnderCheck(Point from, Point to, boolean isWhiteSide) {
        if (isKingInCheck(isWhiteSide)) {
            // Temporarily make the move
            ChessPiece movedPiece = chessBoard.remove(from);
            ChessPiece capturedPiece = chessBoard.put(to, movedPiece);

            boolean stillInCheck = isKingInCheck(isWhiteSide);

            // Undo the move
            chessBoard.put(from, movedPiece);
            if (capturedPiece != null) chessBoard.put(to, capturedPiece);
            else chessBoard.remove(to);

            return !stillInCheck; // Move is legal if it resolves the check
        }
        return true; // If not in check, move legality is determined by piece-specific rules
    }

    public void handleMove(Point from, Point to) {
        ChessPiece movedPiece = chessBoard.get(from);
        if (movedPiece == null || movedPiece.isWhite() != isWhiteTurn) {
            Toast.makeText(this, "Invalid move", Toast.LENGTH_SHORT).show();
            return;
        }


        // Perform the actual move
        chessBoard.remove(from);
        chessBoard.put(to, movedPiece);
        movedPiece.setHasMoved(true);

        // Immediately after moving, check for checkmate
        if (isCheckmate(!isWhiteTurn)) {
            concludeGame(isWhiteTurn ? "White" : "Black");
            return; // Important to return here to not proceed with further logic that might expect the game to continue
        }


        updateBoardView(); // Refresh the board view if the game continues
    }

    private void concludeGame(String winnerName) {
        // Ensure this method is being called by adding log statements or debugging.
        Toast.makeText(getApplicationContext(), winnerName + " wins by checkmate!", Toast.LENGTH_LONG).show();

        // It's a good practice to run UI changes on the UI thread, especially when called from potentially different threads or callbacks.
        runOnUiThread(() -> {
            // Navigate to GameRecordsActivity
            navigateToGameRecords();
        });
    }


    private boolean isKingInCheck(boolean isWhiteSide) {
        // Find the king's position
        Point kingPosition = findKingPosition(isWhiteSide);
        if (kingPosition == null) return false;

        // Check if the king's position is under attack
        return isSquareUnderAttack(kingPosition.x, kingPosition.y, !isWhiteSide);
    }

    private Point findKingPosition(boolean isWhiteSide) {
        for (Map.Entry<Point, ChessPiece> entry : chessBoard.entrySet()) {
            ChessPiece piece = entry.getValue();
            if (piece instanceof King && piece.isWhite() == isWhiteSide) {
                return entry.getKey();
            }
        }
        return null; // King not found (shouldn't happen in a valid game state)
    }

    private boolean attemptCastle(Point kingPosition, Point targetPosition) {
        ChessPiece king = chessBoard.get(kingPosition);
        if (!(king instanceof King) || king.hasMoved()) {
            return false;
        }

        int direction = targetPosition.x - kingPosition.x > 0 ? 1 : -1;
        Point rookPosition = new Point(direction == 1 ? 7 : 0, kingPosition.y);
        ChessPiece rook = chessBoard.get(rookPosition);

        if (!(rook instanceof Rook) || rook.hasMoved()) {
            return false;
        }

        // Check if path is clear
        int x = kingPosition.x + direction;
        while (x != rookPosition.x) {
            if (isSquareOccupied(x, kingPosition.y)) {
                return false;
            }
            x += direction;
        }

        // Move the King and the Rook
        chessBoard.put(targetPosition, king);
        chessBoard.put(new Point(targetPosition.x - direction, targetPosition.y), rook);
        chessBoard.remove(kingPosition);
        chessBoard.remove(rookPosition);

        // Mark King and Rook as having moved
        ((King) king).setHasMoved(true);
        ((Rook) rook).setHasMoved(true);

        updateBoardView();
        return true;
    }
    private void updateBoardView() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ImageView squareImageView = boardViews[y][x];
                ChessPiece piece = chessBoard.get(new Point(x, y));
                if (piece != null) {
                    squareImageView.setImageResource(getDrawableIdForPiece(piece));
                } else {
                    squareImageView.setImageDrawable(null); // Clear the square
                }
            }
        }
    }

    private int getDrawableIdForPiece(ChessPiece piece) {
        if (piece == null) {
            return 0; // No piece present
        }

        // Determine the type and color of the piece to choose the appropriate drawable
        if (piece instanceof Pawn) {
            return piece.isWhite() ? R.drawable.pawn_white : R.drawable.pawn_black;
        } else if (piece instanceof Rook) {
            return piece.isWhite() ? R.drawable.rook_white : R.drawable.rook_black;
        } else if (piece instanceof Knight) {
            return piece.isWhite() ? R.drawable.knight_white : R.drawable.knight_black;
        } else if (piece instanceof Bishop) {
            return piece.isWhite() ? R.drawable.bishop_white : R.drawable.bishop_black;
        } else if (piece instanceof Queen) {
            return piece.isWhite() ? R.drawable.queen_white : R.drawable.queen_black;
        } else if (piece instanceof King) {
            return piece.isWhite() ? R.drawable.king_white : R.drawable.king_black;
        }

        return 0; // Fallback, should not reach here if all cases are covered
    }
    private int getRotatedDrawableIdForPiece(ChessPiece piece) {
        if (piece == null) {
            return 0; // No piece present
        }
        // Example IDs; replace these with your actual drawable resource IDs for rotated pieces
        if (piece instanceof Pawn) {
            return piece.isWhite() ? R.drawable.pawn_white_rotated : R.drawable.pawn_black_rotated;
        } else if (piece instanceof Rook) {
            return piece.isWhite() ? R.drawable.rook_white_rotated : R.drawable.rook_black_rotated;
        } else if (piece instanceof Knight) {
            return piece.isWhite() ? R.drawable.knight_white_rotated : R.drawable.knight_black_rotated;
        } else if (piece instanceof Bishop) {
            return piece.isWhite() ? R.drawable.bishop_white_rotated : R.drawable.bishop_black_rotated;
        } else if (piece instanceof Queen) {
            return piece.isWhite() ? R.drawable.queen_white_rotated : R.drawable.queen_black_rotated;
        } else if (piece instanceof King) {
            return piece.isWhite() ? R.drawable.king_white_rotated : R.drawable.king_black_rotated;
        }
        return 0; // Fallback, should not reach here if all cases are covered
    }

    private void updatePieceImagesForRotation() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ImageView squareImageView = boardViews[y][x];
                ChessPiece piece = chessBoard.get(new Point(x, y));
                if (piece != null) {
                    int drawableId = isBoardRotated ? getRotatedDrawableIdForPiece(piece) : getDrawableIdForPiece(piece);
                    squareImageView.setImageResource(drawableId);
                } else {
                    squareImageView.setImageDrawable(null); // Clear the square if no piece is present
                }
            }
        }
    }
    private void resetHighlight() {
        for (int row = 0; row < boardViews.length; row++) {
            for (int col = 0; col < boardViews[row].length; col++) {
                ImageView square = boardViews[row][col];
                int backgroundColor = (col + row) % 2 == 0 ? ContextCompat.getColor(this, R.color.dark_square) : ContextCompat.getColor(this, R.color.light_square);
                square.setBackgroundColor(backgroundColor);
            }
        }
    }
    public boolean isCheckmate(boolean isWhiteTurn) {
        if (!isKingInCheck(isWhiteTurn)) {
            return false; // If the king isn't in check, it can't be checkmate.
        }

        for (Map.Entry<Point, ChessPiece> entry : chessBoard.entrySet()) {
            ChessPiece piece = entry.getValue();
            Point piecePosition = entry.getKey();

            // Skip pieces not belonging to the current player.
            if (piece.isWhite() != isWhiteTurn) {
                continue;
            }

            // Attempt every possible move for this piece.
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    Point targetPosition = new Point(col, row);

                    // Check if the move is theoretically valid for the piece (ignoring check conditions).
                    if (piece.isValidMove(piecePosition.x, piecePosition.y, col, row)) {
                        // Check if making this move would leave the player in check.
                        if (isMoveLegalUnderCheck(piecePosition, targetPosition, isWhiteTurn)) {
                            // If any move is legal (doesn't result in check), it's not checkmate.
                            return false;
                        }
                    }
                }
            }
        }

        // If we've found no legal moves and the king is in check, it's checkmate.
        return true;
    }
    // BoardStateCallback methods implementation
    @Override
    public boolean isSquareOccupied(int x, int y) {
        boolean occupied = chessBoard.containsKey(new Point(x, y));
        Log.d("ChessDebug", "Checking if square (" + x + ", " + y + ") is occupied: " + occupied);
        return occupied;
    }

    @Override
    public boolean isPathClear(int fromX, int fromY, int toX, int toY) {
        int xDirection = Integer.compare(toX, fromX);
        int yDirection = Integer.compare(toY, fromY);

        int currentX = fromX + xDirection;
        int currentY = fromY + yDirection;

        while (currentX != toX || currentY != toY) {
            if (chessBoard.containsKey(new Point(currentX, currentY))) {
                return false; // Found a piece in the path
            }
            currentX += xDirection;
            currentY += yDirection;
        }

        return true; // Path is clear
    }

    @Override
    public boolean isSquareUnderAttack(int x, int y, boolean isWhiteAttacker) {
        for (Map.Entry<Point, ChessPiece> entry : chessBoard.entrySet()) {
            ChessPiece piece = entry.getValue();
            Point piecePosition = entry.getKey();

            // Exclude the king from the pieces that can attack the square
            if (!(piece instanceof King) && piece.isWhite() == isWhiteAttacker) {
                // Temporarily move the king to the destination to check if it's under attack
                Point originalKingPosition = null;
                if (piece instanceof King) {
                    originalKingPosition = piecePosition; // Save the king's original position
                    // Simulate moving the king to the destination
                    chessBoard.remove(piecePosition);
                    chessBoard.put(new Point(x, y), piece);
                }

                boolean attack = piece.isValidMove(piecePosition.x, piecePosition.y, x, y, true);

                // Move the king back to its original position if it was moved
                if (originalKingPosition != null) {
                    chessBoard.remove(new Point(x, y));
                    chessBoard.put(originalKingPosition, piece);
                }

                if (attack) {
                    return true; // The square is under attack by an opponent's piece
                }
            }
        }
        return false; // The square is not under attack
    }

    @Override
    public ChessPiece getChessPieceAt(int x, int y) {
        // Retrieve the chess piece from the board based on the provided coordinates
        return chessBoard.get(new Point(x, y));
    }


    Deque<Move> moveHistory = new ArrayDeque<>();
    public void makeMove(Point from, Point to) {
        ChessPiece movedPiece = chessBoard.get(from);
        ChessPiece capturedPiece = chessBoard.get(to);
        boolean wasFirstMove = movedPiece != null && !movedPiece.hasMoved();

        boolean wasCastlingMove = false;
        Point castlingRookFrom = null;
        Point castlingRookTo = null;

        // Check if this is a castling move for the king
        if (movedPiece instanceof King && Math.abs(to.x - from.x) == 2) {
            wasCastlingMove = true;
            int direction = to.x > from.x ? 1 : -1;
            castlingRookFrom = new Point((direction == 1) ? 7 : 0, from.y);
            castlingRookTo = new Point(from.x + direction, from.y);

            // Move the rook as part of castling
            ChessPiece rook = chessBoard.remove(castlingRookFrom);
            chessBoard.put(castlingRookTo, rook);
            if (rook != null) {
                rook.setHasMoved(true);
            }
        }

        // Move the king or any other piece
        if (movedPiece != null) {
            movedPiece.setHasMoved(true);
            chessBoard.put(to, movedPiece);
            chessBoard.remove(from);
        }

        // Record the move with details about castling if applicable
        moveHistory.push(new Move(from, to, movedPiece, capturedPiece, wasFirstMove, wasCastlingMove, castlingRookFrom, castlingRookTo));

        updateBoardView(); // Refresh the board UI to reflect the move
    }

    public void undoLastMove() {
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.pop();

            // Restore the moved piece to its original position
            lastMove.movedPiece.setHasMoved(lastMove.wasFirstMove);
            chessBoard.put(lastMove.from, lastMove.movedPiece);

            // If there was a captured piece, restore it
            if (lastMove.capturedPiece != null) {
                chessBoard.put(lastMove.to, lastMove.capturedPiece);
            } else {
                chessBoard.remove(lastMove.to);
            }

            // Handle the specifics of undoing a castling move
            if (lastMove.wasCastlingMove) {
                // The castling move involves moving both the king and the rook
                ChessPiece rook = chessBoard.get(lastMove.castlingRookTo);
                if (rook != null) {
                    rook.setHasMoved(false); // Resetting the rook's hasMoved status
                    chessBoard.put(lastMove.castlingRookFrom, rook); // Moving the rook back
                    chessBoard.remove(lastMove.castlingRookTo);
                }
                // No need to explicitly move the king back; it's already done.
            }

            updateBoardView(); // Refresh the board UI
            togglePlayerTurn(); // Correctly toggle the turn back to the other player
        }
    }

    private void startPlayerOneTimer() {
        playerOneTimer = new CountDownTimer(30000, 50) { // Adjust the timer duration as necessary
            public void onTick(long millisUntilFinished) {
                // Update player one's timer TextView
                playerOneTimerTextView.setText(formatTime(millisUntilFinished));
            }

            public void onFinish() {
                // Insert a record with Player Two as the winner
                insertGameRecord(playerOneName, playerTwoName, "White", "Black", getCurrentDate(), playerTwoName);

                // Display who won and navigate
                Toast.makeText(MainActivity.this, "Player Two Wins by Time!", Toast.LENGTH_LONG).show();
                navigateToGameRecords();
            }
        }.start();

        isPlayerOneTimerRunning = true;
    }
    private void startPlayerTwoTimer() {
        playerTwoTimer = new CountDownTimer(30000, 50) { // Adjust the timer duration as necessary
            public void onTick(long millisUntilFinished) {
                // Update player two's timer TextView
                playerTwoTimerTextView.setText(formatTime(millisUntilFinished));
            }

            public void onFinish() {
                // Insert a record with Player One as the winner
                insertGameRecord(playerOneName, playerTwoName, "White", "Black", getCurrentDate(), playerOneName);

                // Display who won and navigate
                Toast.makeText(MainActivity.this, "Player One Wins by Time!", Toast.LENGTH_LONG).show();
                navigateToGameRecords();
            }
        }.start();
    }
    private String formatTime(long millis) {
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    private void navigateToGameRecords() {
        Intent intent = new Intent(this, GameRecordsActivity.class);
        startActivity(intent);
        finish(); // Optionally finish MainActivity
    }
    private void navigateBackToOpening() {
        Intent intent = new Intent(this, OpeningActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Ensure this activity is closed and removed from the back stack.
    }
    private void insertGameRecord(String playerOneName, String playerTwoName, String playerOneColor, String playerTwoColor, String startDate, String winner) {
        new Thread(() -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(GameDatabaseHelper.COLUMN_PLAYER_ONE_NAME, playerOneName);
            values.put(GameDatabaseHelper.COLUMN_PLAYER_TWO_NAME, playerTwoName);
            values.put(GameDatabaseHelper.COLUMN_PLAYER_ONE_COLOR, playerOneColor);
            values.put(GameDatabaseHelper.COLUMN_PLAYER_TWO_COLOR, playerTwoColor);
            values.put(GameDatabaseHelper.COLUMN_START_DATE, startDate);
            values.put(GameDatabaseHelper.COLUMN_WINNER, winner);

            long newRowId = db.insert(GameDatabaseHelper.TABLE_GAMES, null, values);
            db.close();

            // Optionally update the UI thread with the result
            runOnUiThread(() -> {
                // Update your UI here
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerOneTimer != null) {
            playerOneTimer.cancel();
        }
        if (playerTwoTimer != null) {
            playerTwoTimer.cancel();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Check if the app can schedule exact alarms
        if (canScheduleExactAlarms()) {
            // Schedule the reminder
            scheduleReminder();
        } else {
            // If the permission is not granted, you might want to inform the user.
            // This step is optional and depends on your app's flow and user experience.
            // For example, you could use a Toast, a Snackbar, or a dialog to inform them.
            Toast.makeText(this, "Please enable exact alarm scheduling for reminders.", Toast.LENGTH_LONG).show();
        }
    }

    private void cancelReminder() {
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pendingIntent != null && alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelReminder();
    }

    private void scheduleReminder() {
        Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this,
                0, // Ensure the request code is unique if scheduling multiple alarms
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // For API 31 and above
        );


        // Setting up the AlarmManager to trigger the PendingIntent after 30 seconds
        long thirtySecondsFromNow = System.currentTimeMillis() + 30 * 1000; // 30 seconds in milliseconds
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, thirtySecondsFromNow, pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, thirtySecondsFromNow, pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, thirtySecondsFromNow, pendingIntent);
            }
        }
    }

    private boolean canScheduleExactAlarms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            return alarmManager != null && alarmManager.canScheduleExactAlarms();
        }
        return true; // Below Android 12, no explicit permission required.
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LemubitReminderChannel";
            String description = "Channel for Lemubit Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rotateBoard();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            rotateBoard();
        }
    }
}








