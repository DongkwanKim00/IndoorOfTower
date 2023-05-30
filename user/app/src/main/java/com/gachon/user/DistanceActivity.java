package com.gachon.user;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DistanceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        // Retrieve the distance value from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int distance = extras.getInt("distance", 0); // Replace "distance" with the key used to pass the distance value
            // Display the distance in a TextView or any other UI element
            TextView distanceTextView = findViewById(R.id.distanceTextView); // Replace R.id.distanceTextView with the actual ID of your TextView
            distanceTextView.setText("Manhattan Distance: " + distance);
        }
    }
}
