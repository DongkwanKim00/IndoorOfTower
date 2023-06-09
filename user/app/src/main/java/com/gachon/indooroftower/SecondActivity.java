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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private TextView wifiTextView;

    // 장애물 좌표들을 저장할 변수
    private List<Point> obstacleCoordinates;

    // 기존에 저장된 AP들의 정보(나중에 데베에서 가져오게 되면 AP정보들을 넣는다고 가정.)
    List<Point> savedAPs = new ArrayList<>();

    // 출발지와 목적지 좌표
    private double x1, y1; // 출발지 좌표, 데베에 저장된 형식 참고해서 KNN돌려서 구해야함.
    private double x2, y2; // 목적지 좌표, 호실을 int형으로 number변수에서 알수있음. 이거 이용해서 호실의 rssi 등 값들 이용해야함.

    // 주기적인 스캔을 위한 핸들러와 Runnable
    private Handler handler;
    private Runnable scanRunnable;

    // 센서 관련 변수
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private Sensor magnetometer;
    private float[] gyroValues;
    private float[] magnetValues;
    private float[] orientationValues = new float[3];

    // Toast 메시지에 표시할 숫자(5초마다 와이파이 정보 잘 불러오는지 테스트 할 토스트메시지)
    private int toastNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 장애물 좌표 초기화. 예시로 적어둔겁니다. 이런식으로 장애물 좌표 넣기.
        obstacleCoordinates = new ArrayList<>();
        obstacleCoordinates.add(new Point(3, 0));
        obstacleCoordinates.add(new Point(7, 2));
        obstacleCoordinates.add(new Point(5, 6));
        obstacleCoordinates.add(new Point(2, 9));
        obstacleCoordinates.add(new Point(8, 5));
        //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ

        wifiTextView = findViewById(R.id.wifiTextView);
        TextView textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        if (intent != null) {
            String strText = intent.getStringExtra("strText");
            if (strText != null) {
                String numberString = strText.substring(0, 3); // 앞 3자리 추출
                int number = Integer.parseInt(numberString); // 문자열을 숫자로 변환
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

        // 센서 매니저 초기화
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 센서 리스너 등록
        sensorManager.registerListener(sensorEventListener, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 센서 리스너 해제
        sensorManager.unregisterListener(sensorEventListener);
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

        // 모든 AP 정보 표시
        StringBuilder wifiInfoBuilder = new StringBuilder();
        for (ScanResult scanResult : scanResults) {
            String ssid = scanResult.SSID;
            String bssid = scanResult.BSSID;
            int rssi = scanResult.level;

            wifiInfoBuilder.append("SSID: ").append(ssid).append("\n")
                    .append("BSSID: ").append(bssid).append("\n")
                    .append("RSSI: ").append(rssi).append("\n\n");
        }

        wifiTextView.setText(wifiInfoBuilder.toString());


        // 맨해튼 거리 계산
        double manhattanDistance = calculateManhattanDistance(x1, y1, x2, y2);

        // 방향 계산
        double azimuth = Math.toDegrees(orientationValues[0]);
        // 방향에 따라 필요한 조치를 취하도록 로직을 구현해주세요.

        // Toast 메시지 표시
        toastNumber++; // 숫자 증가
        Toast.makeText(this, "와이파이 정보 불러오기: " + toastNumber + "번째", Toast.LENGTH_SHORT).show();
    }

    // 맨해튼 거리 계산 함수
    private double calculateManhattanDistance(double x1, double y1, double x2, double y2) {
        // 장애물을 피해가는 맨해튼 거리 계산
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);

        // 장애물을 피해가기 위한 보정값 추가
        for (Point obstacle : obstacleCoordinates) {
            double obstacleX = obstacle.x;
            double obstacleY = obstacle.y;
            if (x1 <= obstacleX && obstacleX <= x2 && y1 <= obstacleY && obstacleY <= y2) {
                // 출발지와 목적지 사이에 장애물이 있는 경우 보정값 추가
                dx += Math.abs(obstacleX - x1) + Math.abs(obstacleX - x2);
                dy += Math.abs(obstacleY - y1) + Math.abs(obstacleY - y2);
            }
        }

        // 맨해튼 거리 계산
        return dx + dy;
    }

    // 센서 이벤트 리스너
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor == gyroscope) {
                gyroValues = event.values;
            } else if (event.sensor == magnetometer) {
                magnetValues = event.values;
            }

            if (gyroValues != null && magnetValues != null) {
                // 회전 매트릭스 계산
                float[] rotationMatrix = new float[9];
                boolean success = SensorManager.getRotationMatrix(rotationMatrix, null, gyroValues, magnetValues);

                if (success) {
                    // 방향 계산
                    SensorManager.getOrientation(rotationMatrix, orientationValues);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // 필요한 경우 정확도 변경에 대한 이벤트를 처리할 수 있습니다.
        }
    };

    public class Point {
        private double x;
        private double y;

        public Point() { // 기본 생성자

        }

        public Point(double x, double y) {	// 이와 같이 생성자를 정의하면 new 호출시 필드 값 초기화가 가능하다.
            this.x = x;
            this.y = y;
        }

    }
}
