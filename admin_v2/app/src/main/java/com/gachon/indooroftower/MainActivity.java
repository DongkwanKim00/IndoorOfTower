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

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText textInput;
    private EditText xCoordinateInput;
    private EditText yCoordinateInput;
    private Button measureButton;
    private Button addButton;
    private LinearLayout wifiListLayout;

    private FirebaseFirestore db;
    private CollectionReference dataCollection;

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

        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();
        dataCollection = db.collection("RP");

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
                uploadDataToFirestore();
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
                Toast.makeText(this, "권한이 거부되었습니다", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void measureWiFi() {
        String text = textInput.getText().toString();
        String xCoordinate = xCoordinateInput.getText().toString();
        String yCoordinate = yCoordinateInput.getText().toString();

        // WiFi 스캐닝 및 측정 로직 수행
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            textView.setText("SSID: " + ssid + "\nBSSID: " + bssid + "\nRSSI: " + rssi);

            wifiListLayout.addView(textView);
        }

        // 스크롤을 리스트의 가장 아래로 이동
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

//    private void uploadDataToFirestore() {
//        String key = textInput.getText().toString();
//        String xCoordinate = xCoordinateInput.getText().toString();
//        String yCoordinate = yCoordinateInput.getText().toString();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        List<ScanResult> wifiList = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getScanResults();
//
//        // 첫 번째 텍스트 상자의 값을 키로 사용하여 새 문서 생성
//        DocumentReference documentRef = dataCollection.document(key);
//
//        // 값을 설정하기 위한 데이터 객체 생성
//        Map<String, Object> data = new HashMap<>();
//        data.put("roomNumber", key);
//        data.put("xCoordinate", xCoordinate);
//        data.put("yCoordinate", yCoordinate);
//
//        // 값들을 문서에 설정
//        documentRef.set(data)
//                .addOnSuccessListener(aVoid -> {
//                    // 문서 저장 성공
//                    uploadWiFiListToFirestore(documentRef, wifiList);
//                    Toast.makeText(this, "데이터가 Firestore에 업로드되었습니다", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    // 에러 처리
//                    Toast.makeText(this, "Firestore에 데이터 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
//                });
//    }

    private void uploadDataToFirestore() {
        String key = textInput.getText().toString();
        String xCoordinate = xCoordinateInput.getText().toString();
        String yCoordinate = yCoordinateInput.getText().toString();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<ScanResult> wifiList = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getScanResults();

        // 첫 번째 텍스트 상자의 값을 키로 사용하여 새 문서 생성
        DocumentReference documentRef = dataCollection.document(key);

        // 값을 설정하기 위한 데이터 객체 생성
        Map<String, Object> data = new HashMap<>();
        data.put("roomNumber", key);
        data.put("xCoordinate", xCoordinate);
        data.put("yCoordinate", yCoordinate);

        // Wi-Fi 리스트 데이터 추가
        for (ScanResult result : wifiList) {
            String rssi = String.valueOf(result.level);
            String bssid = result.BSSID;
            String ssid = result.SSID;

            data.put(ssid + "_rssi", rssi);
            data.put(ssid + "_bssid", bssid);
        }

        // 값들을 문서에 설정
        documentRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    // 문서 저장 성공
                    Toast.makeText(this, "데이터가 Firestore에 업로드되었습니다", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // 에러 처리
                    Toast.makeText(this, "Firestore에 데이터 업로드에 실패했습니다", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadWiFiListToFirestore(DocumentReference documentRef, List<ScanResult> wifiList) {
        CollectionReference wifiCollectionRef = documentRef.collection("wifiList");

        for (ScanResult result : wifiList) {
            String rssi = String.valueOf(result.level);
            String bssid = result.BSSID;
            String ssid = result.SSID;

            Map<String, Object> wifiData = new HashMap<>();
            wifiData.put("ssid", ssid);
            wifiData.put("bssid", bssid);
            wifiData.put("rssi", rssi);

            wifiCollectionRef.document(ssid).set(wifiData);
        }
    }
}
