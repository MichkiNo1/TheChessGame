package com.example.thechessgame;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class GameRecordsActivity extends AppCompatActivity {
    private GameDatabaseHelper dbHelper;
    private ScrollView scrollViewRecords;
    private LinearLayout recordsContainer;
    private FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_records);

        fragmentContainer = findViewById(R.id.fragment_container);
        scrollViewRecords = findViewById(R.id.svGameRecords);
        recordsContainer = findViewById(R.id.records_container);

        dbHelper = new GameDatabaseHelper(this);
        loadGameRecords();

        Button backToOpeningButton = findViewById(R.id.buttonBackToOpening);
        backToOpeningButton.setOnClickListener(v -> navigateBackToOpening());

        Button searchGamesButton = findViewById(R.id.buttonSearchGames);
        searchGamesButton.setOnClickListener(v -> openSearchFragment());
    }

    private void openSearchFragment() {
        fragmentContainer.setVisibility(View.VISIBLE);
        scrollViewRecords.setVisibility(View.GONE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SearchGamesFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadGameRecords() {
        Cursor cursor = dbHelper.getAllGames();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String record = String.format("Date: %s, Winner: %s, Player 1: %s, Player 2: %s",
                        cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_START_DATE)),
                        cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_WINNER)),
                        cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_PLAYER_ONE_NAME)),
                        cursor.getString(cursor.getColumnIndex(GameDatabaseHelper.COLUMN_PLAYER_TWO_NAME)));
                TextView recordView = new TextView(this);
                recordView.setText(record);
                recordsContainer.addView(recordView);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void navigateBackToOpening() {
        Intent intent = new Intent(this, OpeningActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                scrollViewRecords.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
