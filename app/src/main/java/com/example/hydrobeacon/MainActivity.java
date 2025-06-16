package com.example.hydrobeacon;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar; // Import ProgressBar
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView waterLevelText;
    ProgressBar waterLevelProgress;
    final int LOW_THRESHOLD = 20;
    final int HIGH_THRESHOLD = 80;
    int lastNotifiedLevel = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        waterLevelText = findViewById(R.id.waterLevelText);
        waterLevelProgress = findViewById(R.id.waterLevelProgress); // Initialize ProgressBar

        String firebaseUrl = "https://hydrobeacon-default-rtdb.asia-southeast1.firebasedatabase.app";

        FirebaseDatabase.getInstance(firebaseUrl)
                .getReference("sensorData/waterLevel")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Double levelDouble = snapshot.getValue(Double.class);
                        int level = levelDouble != null ? (int) Math.floor(levelDouble) : 0;


                        waterLevelText.setText(level + "%");


                        waterLevelProgress.setProgress(level);

                        Log.d("FirebaseLevel", "Water Level: " + level + "%, Progress: " + waterLevelProgress.getProgress());


                        if (level < LOW_THRESHOLD && lastNotifiedLevel >= LOW_THRESHOLD) {
                            showToast("Water level too low! (" + level + "%)");
                        } else if (level > HIGH_THRESHOLD && lastNotifiedLevel <= HIGH_THRESHOLD) {
                            showToast("Water level too high! (" + level + "%)");
                        }

                        lastNotifiedLevel = level;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Database error: " + error.getMessage());
                    }
                });
    }

    void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
