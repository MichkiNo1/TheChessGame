package com.example.thechessgame;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SearchGamesFragment extends Fragment {
    private EditText playerOneEditText, playerTwoEditText;
    private LinearLayout resultsContainer;
    private Switch switchMode;
    private GameDatabaseHelper dbHelper;
    private Button showAllGamesButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_search_games_fragment, container, false);

        // Initialize your views
        playerOneEditText = view.findViewById(R.id.playerOneEditText);
        playerTwoEditText = view.findViewById(R.id.playerTwoEditText);
        resultsContainer = view.findViewById(R.id.resultsContainer);
        switchMode = view.findViewById(R.id.switchMode);
        showAllGamesButton = view.findViewById(R.id.showAllGamesButton);

        dbHelper = new GameDatabaseHelper(getContext());
        Button searchButton = view.findViewById(R.id.searchButton);

        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            playerTwoEditText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        searchButton.setOnClickListener(v -> performSearch());

        // Set up the Show All Games button
        showAllGamesButton.setOnClickListener(v -> loadAllGames());

        return view;
    }

    private void performSearch() {
        String playerOneName = playerOneEditText.getText().toString().trim();
        String playerTwoName = playerTwoEditText.getText().toString().trim();

        if (switchMode.isChecked()) {
            if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter names for both players", Toast.LENGTH_SHORT).show();
                return;
            }
            searchGamesByTwoPlayers(playerOneName, playerTwoName);
        } else {
            if (playerOneName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter name for player one", Toast.LENGTH_SHORT).show();
                return;
            }
            searchGamesByOnePlayer(playerOneName);
        }
    }


    private void loadAllGames() {
        Cursor cursor = dbHelper.getAllGames();
        displayResults(cursor);
    }

    private void searchGamesByOnePlayer(String playerName) {
        Cursor cursor = dbHelper.getGamesByPlayer(playerName);
        displayResults(cursor);
    }

    private void searchGamesByTwoPlayers(String playerOne, String playerTwo) {
        Cursor cursor = dbHelper.getGamesByPlayers(playerOne, playerTwo);
        displayResults(cursor);
    }

    private void displayResults(Cursor cursor) {
        resultsContainer.removeAllViews(); // Clear previous results
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String record = createRecordString(cursor);
                TextView recordView = new TextView(getContext());
                recordView.setText(record);
                resultsContainer.addView(recordView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private String createRecordString(Cursor cursor) {
        String date = cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_START_DATE));
        String winner = cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_WINNER));
        String playerOne = cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_PLAYER_ONE_NAME));
        String playerTwo = cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_PLAYER_TWO_NAME));

        return "Date: " + date + ", Winner: " + winner + ", Player 1: " + playerOne + ", Player 2: " + playerTwo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}

