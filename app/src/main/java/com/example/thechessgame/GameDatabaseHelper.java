package com.example.thechessgame;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "gameDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_GAMES = "games";

    // Game Table Columns
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLAYER_ONE_NAME = "playerOneName";
    public static final String COLUMN_PLAYER_TWO_NAME = "playerTwoName";
    public static final String COLUMN_PLAYER_ONE_COLOR = "playerOneColor";
    public static final String COLUMN_PLAYER_TWO_COLOR = "playerTwoColor";
    public static final String COLUMN_START_DATE = "startDate";
    public static final String COLUMN_WINNER = "winner";

    // SQL to create table

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_GAMES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PLAYER_ONE_NAME + " TEXT NOT NULL, " +
                    COLUMN_PLAYER_TWO_NAME + " TEXT NOT NULL, " +
                    COLUMN_PLAYER_ONE_COLOR + " TEXT NOT NULL, " +
                    COLUMN_PLAYER_TWO_COLOR + " TEXT NOT NULL, " +
                    COLUMN_START_DATE + " TEXT NOT NULL, " +
                    COLUMN_WINNER + " TEXT);";

    public GameDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public Cursor getGamesByPlayer(String playerName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_START_DATE, COLUMN_WINNER, COLUMN_PLAYER_ONE_NAME, COLUMN_PLAYER_TWO_NAME, COLUMN_PLAYER_ONE_COLOR, COLUMN_PLAYER_TWO_COLOR};
        String selection = COLUMN_PLAYER_ONE_NAME + "=? OR " + COLUMN_PLAYER_TWO_NAME + "=?";
        String[] selectionArgs = {playerName, playerName}; // Search for games where the player was either player one or player two
        return db.query(TABLE_GAMES, columns, selection, selectionArgs, null, null, COLUMN_START_DATE + " DESC");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for game data, so its upgrade policy is
        // to simply discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GAMES);
        onCreate(db);
    }
    public Cursor getAllGames() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GAMES, null);
    }

    public Cursor getGamesByPlayers(String playerOneName, String playerTwoName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_START_DATE, COLUMN_WINNER, COLUMN_PLAYER_ONE_NAME, COLUMN_PLAYER_TWO_NAME, COLUMN_PLAYER_ONE_COLOR, COLUMN_PLAYER_TWO_COLOR};
        String selection = COLUMN_PLAYER_ONE_NAME + "=? AND " + COLUMN_PLAYER_TWO_NAME + "=? OR " + COLUMN_PLAYER_ONE_NAME + "=? AND " + COLUMN_PLAYER_TWO_NAME + "=?";
        String[] selectionArgs = {playerOneName, playerTwoName, playerTwoName, playerOneName}; // Search for both combinations of player names
        return db.query(TABLE_GAMES, columns, selection, selectionArgs, null, null, null);
    }

}


