package com.gachon.user;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DistanceActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        // Retrieve the distance value from the intent extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int distance = extras.getInt("distance", 0); // Replace "distance" with the key used to pass the distance value
            // Display the distance in a TextView or any other UI element
            TextView distanceTextView = findViewById(R.id.distanceTextView); // Replace R.id.distanceTextView with the actual ID of your TextView
            distanceTextView.setText("Manhattan Distance: " + distance);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }



    private SensorEventListener sensorEventListener = new SensorEventListener() {
        float[] accelerometerValues = new float[3];
        float[] magnetometerValues = new float[3];

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == accelerometerSensor) {
                accelerometerValues = event.values.clone();
            } else if (event.sensor == magnetometerSensor) {
                magnetometerValues = event.values.clone();
            }

            float[] rotationMatrix = new float[9];
            boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerValues, magnetometerValues);
            if (success) {
                float[] orientationValues = new float[3];
                SensorManager.getOrientation(rotationMatrix, orientationValues);
                // orientationValues[0] contains the azimuth (rotation around the Z-axis)

                // Calculate the direction difference between destination and device orientation
                float destinationDirection = calculateDestinationDirection(); // Replace this with your own logic to get the destination direction
                float deviceDirection = orientationValues[0];
                float directionDifference = calculateDirectionDifference(destinationDirection, deviceDirection);

                // Update the UI based on the direction difference
                updateDirectionUI(directionDifference);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Do nothing
        }
    };

    // Method to calculate the direction difference between destination and device orientation
    private float calculateDirectionDifference(float destinationDirection, float deviceDirection) {
        // Calculate the difference between the two directions
        float difference = destinationDirection - deviceDirection;

        // Normalize the difference to be within the range of -180 to 180 degrees
        if (difference > 180) {
            difference -= 360;
        } else if (difference < -180) {
            difference += 360;
        }

        return difference;
    }

    // Method to calculate the destination direction (Replace with your own logic)
    private float calculateDestinationDirection() {
        // Replace with your own logic to calculate the destination direction
        // This can involve using the destination coordinates and the user's current location
        // to determine the angle or direction to the destination

        // For example, you can use the atan2 function to calculate the angle:
        // float destinationX = destinationCoordinates.first;
        // float destinationY = destinationCoordinates.second;
        // float userX = userLocationCoordinates.first;
        // float userY = userLocationCoordinates.second;
        // float deltaX = destinationX - userX;
        // float deltaY = destinationY - userY;
        // float destinationDirection = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        // return destinationDirection;

        // Replace the return statement with your own calculation
        return 0.0f;
    }

    
    //아마 이 코드가 방향 UI업데이트 같네요
    // Method to update the UI based on the direction difference
    private void updateDirectionUI(float directionDifference) {
        // Update the UI based on the direction difference value
        // For example, you can update a TextView with the direction information

        TextView directionTextView = findViewById(R.id.directionTextView);

        if (directionDifference > 45) {
            directionTextView.setText("Turn left");
        } else if (directionDifference < -45) {
            directionTextView.setText("Turn right");
        } else {
            directionTextView.setText("Continue straight");
        }
    }


}
