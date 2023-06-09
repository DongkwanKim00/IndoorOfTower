package com.gachon.indooroftower;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText textInput;
    private EditText xCoordinateInput;
    private EditText yCoordinateInput;
    private Button measureButton;
    private Button addButton;
    private LinearLayout wifiListLayout;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = findViewById(R.id.textInput);
        xCoordinateInput = findViewById(R.id.xCoordinateInput);
        yCoordinateInput = findViewById(R.id.yCoordinateInput);
        measureButton = findViewById(R.id.measureButton);
        addButton = findViewById(R.id.addButton);
        wifiListLayout = findViewById(R.id.wifiListLayout);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    measureWiFi();
                } else {
                    requestPermissions();
                }
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDataToFirebase();
            }
        });
    }

    private boolean checkPermissions() {
        int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                measureWiFi();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void measureWiFi() {
        String text = textInput.getText().toString();
        String xCoordinate = xCoordinateInput.getText().toString();
        String yCoordinate = yCoordinateInput.getText().toString();

        // Do your WiFi scanning and measurement logic here
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> wifiList = wifiManager.getScanResults();

        displayWiFiList(wifiList);
    }

    private void displayWiFiList(List<ScanResult> wifiList) {
        wifiListLayout.removeAllViews();

        for (ScanResult result : wifiList) {
            String rssi = String.valueOf(result.level);
            String bssid = result.BSSID;
            String ssid = result.SSID;

            TextView textView = new TextView(this);
            textView.setText("SSID: " + ssid+ "\nBSSID: " + bssid +"\nRSSI: " + rssi  );

            wifiListLayout.addView(textView);
        }

        // Scroll to the bottom of the list
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void uploadDataToFirebase() {
        String key = textInput.getText().toString();
        String xCoordinate = xCoordinateInput.getText().toString();
        String yCoordinate = yCoordinateInput.getText().toString();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<ScanResult> wifiList = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getScanResults();

        // Create a new child node in the Firebase database with the key as the value of the first text box
        DatabaseReference childRef = databaseReference.child(key);

        // Set the values to the child node
        childRef.child("roomNumber").setValue(key);
        childRef.child("xCoordinate").setValue(xCoordinate);
        childRef.child("yCoordinate").setValue(yCoordinate);

        // Upload the Wi-Fi information as a separate child node
        DatabaseReference wifiRef = childRef.child("wifiList");
        for (ScanResult result : wifiList) {
            String rssi = String.valueOf(result.level);
            String bssid = result.BSSID;
            String ssid = result.SSID;

            DatabaseReference wifiChildRef = wifiRef.child(ssid);
            wifiChildRef.child("ssid").setValue(ssid);
            wifiChildRef.child("bssid").setValue(bssid);
            wifiChildRef.child("rssi").setValue(rssi);

        }

        Toast.makeText(this, "Data uploaded to Firebase", Toast.LENGTH_SHORT).show();
    }
}
