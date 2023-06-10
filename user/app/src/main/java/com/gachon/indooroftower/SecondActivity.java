package com.gachon.indooroftower;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecondActivity extends AppCompatActivity {

    private TextView wifiTextView;
    int number; //사용자가 401호를 누르면 이 변수에 숫자 401이 저장됨. 네비게이션 구현할 때 목적지로 사용.
    String numberString;
    TextView wifiList;

    // Firebase Realtime Database의 레퍼런스 가져오기
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // 원하는 키의 prefix
    String prefixKey;

    // 주기적인 스캔을 위한 핸들러와 Runnable
    private Handler handler;
    private Runnable scanRunnable;


    // Toast 메시지에 표시할 숫자(5초마다 와이파이 정보 잘 불러오는지 테스트 할 토스트메시지)
    private int toastNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);


//        wifiTextView = findViewById(R.id.wifiTextView);
        TextView textView = findViewById(R.id.textView);
        wifiList=findViewById(R.id.wifiList);

        Intent intent = getIntent();
        if (intent != null) {
            String strText = intent.getStringExtra("strText");
            if (strText != null) {
                numberString = strText.substring(0, 3); // 앞 3자리 추출
                number = Integer.parseInt(numberString); // 문자열을 숫자로 변환
                textView.setText(String.valueOf(number)); // 숫자로 변환한 값을 텍스트뷰에 설정
            }
        }

        // 주기적인 스캔을 위한 핸들러와 Runnable 초기화
        handler = new Handler();
        scanRunnable = new Runnable() {
            @Override
            public void run() {
                scanWifi(); // Wi-Fi 스캔 실행
                handler.postDelayed(this, 5000); // 5초마다 스캔을 반복하기 위해 5000ms(5초) 지연 후 다시 호출
            }
        };

        // 스캔 시작
        handler.post(scanRunnable);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료되면 주기적인 스캔 중지
        handler.removeCallbacks(scanRunnable);
    }

    // Wi-Fi 스캔 실행 함수
    private void scanWifi() {
        // Wi-Fi 매니저 초기화
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // 사용자에게 위치 정보를 받을 것인지 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // Wi-Fi 스캔 실행
        wifiManager.startScan();

        // 스캔 결과 가져오기
        List<ScanResult> scanResults = wifiManager.getScanResults();
        System.out.println(scanResults);
        //KNNLocationFinder knnLocalization = new KNNLocationFinder(scanResults);






        // 모든 AP 정보 표시
//        StringBuilder wifiInfoBuilder = new StringBuilder();
//        for (ScanResult scanResult : scanResults) {
//            String ssid = scanResult.SSID;
//            String bssid = scanResult.BSSID;
//            int rssi = scanResult.level;
//
//            wifiInfoBuilder.append("SSID: ").append(ssid).append("\n")
//                    .append("BSSID: ").append(bssid).append("\n")
//                    .append("RSSI: ").append(rssi).append("\n\n");
//        }

//        wifiTextView.setText(wifiInfoBuilder.toString());

        // Toast 메시지 표시
//        toastNumber++; // 숫자 증가
//        Toast.makeText(this, "와이파이 정보 불러오기: " + toastNumber + "번째", Toast.LENGTH_SHORT).show();

        //prefixKey = numberString;

        //fetchDataFromFirebase();
    }



//    private void fetchDataFromFirebase() {
//
////        Query query = databaseReference.orderByKey().startAt(prefixKey).endAt(prefixKey + "\uf8ff");
//        Query query = databaseReference.orderByKey().startAt(prefixKey).endAt(prefixKey + "\uf8ff");
//        query.get().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                List<DataItem> dataItems = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
//                    DataItem temp = dataSnapshot.getValue(DataItem.class);
//                    dataItems.add(temp);
//                }
//                // dataItems 리스트를 사용하여 필요한 작업을 수행하세요
//                System.out.println("@@@@@@@@@@@@@@@@@@@ "+ dataItems.toString());
//            } else {
//                Exception exception = task.getException();
//                if (exception != null) {
//                    // 오류 처리
//                }
//            }
//        });
//    }
//
//    public class DataItem {
//        private String roomNumber;
//        private Map<String, WifiData> wifiList;
//        private String xCoordinate;
//        private String yCoordinate;
//
//        // Empty constructor (required for Firebase)
//        public DataItem() {
//        }
//
//        public DataItem(String roomNumber, Map<String, WifiData> wifiList, String xCoordinate, String yCoordinate) {
//            this.roomNumber = roomNumber;
//            this.wifiList = wifiList;
//            this.xCoordinate = xCoordinate;
//            this.yCoordinate = yCoordinate;
//        }
//
//        // Getter and Setter methods
//
//        public String getRoomNumber() {
//            return roomNumber;
//        }
//
//        public void setRoomNumber(String roomNumber) {
//            this.roomNumber = roomNumber;
//        }
//
//        public Map<String, WifiData> getWifiList() {
//            return wifiList;
//        }
//
//        public void setWifiList(Map<String, WifiData> wifiList) {
//            this.wifiList = wifiList;
//        }
//
//        public String getXCoordinate() {
//            return xCoordinate;
//        }
//
//        public void setXCoordinate(String xCoordinate) {
//            this.xCoordinate = xCoordinate;
//        }
//
//        public String getYCoordinate() {
//            return yCoordinate;
//        }
//
//        public void setYCoordinate(String yCoordinate) {
//            this.yCoordinate = yCoordinate;
//        }
//    }
//
//    public class WifiData {
//        private String bssid;
//        private String rssi;
//        private String ssid;
//
//        public WifiData() {
//        }
//
//        public WifiData(String bssid, String rssi, String ssid) {
//            this.bssid = bssid;
//            this.rssi = rssi;
//            this.ssid = ssid;
//        }
//
//        // Getter and Setter methods
//
//        public String getBssid() {
//            return bssid;
//        }
//
//        public void setBssid(String bssid) {
//            this.bssid = bssid;
//        }
//
//        public String getRssi() {
//            return rssi;
//        }
//
//        public void setRssi(String rssi) {
//            this.rssi = rssi;
//        }
//
//        public String getSsid() {
//            return ssid;
//        }
//
//        public void setSsid(String ssid) {
//            this.ssid = ssid;
//        }
//    }

}


