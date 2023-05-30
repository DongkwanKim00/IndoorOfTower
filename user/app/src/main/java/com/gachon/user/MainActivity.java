package com.gachon.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<Pair<Integer, Integer>> obstacleCoordinates = new ArrayList<>(); //서버에서 장애물(빈 공간)에 대한 x,y좌표 값을 받아오면 여기 arrayList에 넣는다.
    // Add obstacle coordinates to the list as needed
    //obstacleCoordinates.add(new Pair<>(x1, y1)); 예시입니다.
    //obstacleCoordinates.add(new Pair<>(x2, y2));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<String> buttonLabels = new ArrayList<>();
        Map<String, Pair<Integer, Integer>> classroomCoordinates = new HashMap<>();

        // Add button labels and corresponding coordinates
        for (int i = 401; i <= 435; i++) {
            buttonLabels.add(String.valueOf(i));
            // Replace the values below with actual coordinates for each classroom
            int classroomX = getCoordinateXFromServer(i); // Replace with the method to retrieve the x coordinate of the classroom from the server
            int classroomY = getCoordinateYFromServer(i); // Replace with the method to retrieve the y coordinate of the classroom from the server
            classroomCoordinates.put(String.valueOf(i), new Pair<>(classroomX, classroomY));
        }

        // Replace with the method to retrieve the destination coordinates from the server or data source
        int destinationX = getDestinationCoordinateX();
        int destinationY = getDestinationCoordinateY();
        // Replace with the method to retrieve the user's location coordinates from the server or data source
        int userLocationX = getUserCoordinateX();
        int userLocationY = getUserCoordinateY();

        Pair<Integer, Integer> destinationCoordinates = new Pair<>(destinationX, destinationY);
        Pair<Integer, Integer> userLocationCoordinates = new Pair<>(userLocationX, userLocationY);

        ButtonAdapter buttonAdapter = new ButtonAdapter(buttonLabels, classroomCoordinates, destinationCoordinates, userLocationCoordinates, obstacleCoordinates);
        recyclerView.setAdapter(buttonAdapter);




    }
}