package com.example.thechessgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class OpeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening); // Make sure to create a layout file named 'activity_opening.xml'

        // Initialize UI components here (if any)
        setupUI();
    }

    private void setupUI() {
        // Find the "New Game" button and set its click listener
        findViewById(R.id.newGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewGameClicked(view);
            }
        });
        findViewById(R.id.buttonAlarmManager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onSetAlarmClicked(view);}
        });

        findViewById(R.id.gameRecord).setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
               onGameRecordClicked(view);
            }
        });
    }


    // Called when the "New Game" button is clicked
    public void onNewGameClicked(View view) {
        Intent intent = new Intent(OpeningActivity.this, EnterNamesActivity.class);
        startActivity(intent);
    }
    public void onSetAlarmClicked(View view) {
        Intent intent = new Intent(OpeningActivity.this, AlarmManagerActivity.class);
        startActivity(intent);
    }
    public void onGameRecordClicked(View view) {
         Intent intent = new Intent(this, GameRecordsActivity.class); // Assuming you have an InstructionsActivity
         startActivity(intent);
    }

    // Add other methods as needed for your application
}
