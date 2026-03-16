package com.sadi.hydrobeacon.data.remote;

import androidx.annotation.NonNull;

import com.sadi.hydrobeacon.data.model.WaterLevel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDataSource {
    private static final String FIREBASE_URL = "https://hydrobeacon-default-rtdb.asia-southeast1.firebasedatabase.app";
    private final DatabaseReference databaseReference;
    private ValueEventListener listener;

    public interface WaterLevelCallback {
        void onDataChange(WaterLevel waterLevel);
        void onError(Exception e);
    }

    public FirebaseDataSource() {
        this.databaseReference = FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference("sensorData/waterLevel");
    }

    public void startListening(WaterLevelCallback callback) {
        if (listener != null) return;

        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double level = snapshot.getValue(Double.class);
                if (level != null) {
                    WaterLevel waterLevel = new WaterLevel(level, System.currentTimeMillis());
                    callback.onDataChange(waterLevel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        };

        databaseReference.addValueEventListener(listener);
    }

    public void stopListening() {
        if (listener != null) {
            databaseReference.removeEventListener(listener);
            listener = null;
        }
    }
}
