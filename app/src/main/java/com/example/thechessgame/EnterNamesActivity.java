package com.example.thechessgame;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class EnterNamesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_names);

        EditText playerOneNameEditText = findViewById(R.id.playerOneName);
        EditText playerTwoNameEditText = findViewById(R.id.playerTwoName);
        RadioGroup playerOneColorGroup = findViewById(R.id.playerOneColor);
        RadioGroup playerTwoColorGroup = findViewById(R.id.playerTwoColor);
        Button startGameButton = findViewById(R.id.startGameButton);

        startGameButton.setOnClickListener(v -> {
            String playerOneName = playerOneNameEditText.getText().toString();
            String playerTwoName = playerTwoNameEditText.getText().toString();

            if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                Toast.makeText(this, "Please enter names for both players", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine colors based on selection or randomize if both are Auto
            boolean playerOneIsWhite = isPlayerOneWhite(playerOneColorGroup, playerTwoColorGroup);
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("playerOneName", playerOneName);
            intent.putExtra("playerTwoName", playerTwoName);
            intent.putExtra("playerOneIsWhite", playerOneIsWhite);
            startActivity(intent);
        });
    }

    private boolean isPlayerOneWhite(RadioGroup playerOneColorGroup, RadioGroup playerTwoColorGroup) {
        // Example logic to determine color or randomize
        // You might need to adjust this based on your exact requirements
        if (playerOneColorGroup.getCheckedRadioButtonId() == R.id.playerOneWhite) {
            return true;
        } else if (playerTwoColorGroup.getCheckedRadioButtonId() == R.id.playerTwoWhite) {
            return false;
        } else {
            // Randomize if both selected Auto
            return new Random().nextBoolean();
        }
    }
}