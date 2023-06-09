package com.gachon.indooroftower;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private EditText textInput;
    private EditText xCoordinateInput;
    private EditText yCoordinateInput;
    private Button measureButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        textInput = findViewById(R.id.textInput);
        xCoordinateInput = findViewById(R.id.xCoordinateInput);
        yCoordinateInput = findViewById(R.id.yCoordinateInput);
        measureButton = findViewById(R.id.measureButton);

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textInput.getText().toString();
                double xCoordinate = Double.parseDouble(xCoordinateInput.getText().toString());
                double yCoordinate = Double.parseDouble(yCoordinateInput.getText().toString());

            }
        });
    }

}
