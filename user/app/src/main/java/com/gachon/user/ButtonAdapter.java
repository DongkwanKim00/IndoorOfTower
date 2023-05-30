package com.gachon.user;

import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class ButtonAdapter extends RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder> {
    private List<String> buttonLabels;
    private Map<String, Pair<Integer, Integer>> classroomCoordinates;
    private Pair<Integer, Integer> destinationCoordinates; //목적지 좌표 저장
    private Pair<Integer, Integer> userLocationCoordinates; //유저 위치 저장
    private List<Pair<Integer, Integer>> obstacleCoordinates; //장애물 위치 저장된 배열

    public ButtonAdapter(List<String> buttonLabels, Map<String, Pair<Integer, Integer>> classroomCoordinates,
                         Pair<Integer, Integer> destinationCoordinates, Pair<Integer, Integer> userLocationCoordinates, List<Pair<Integer, Integer>> obstacleCoordinates) {
        this.buttonLabels = buttonLabels;
        this.classroomCoordinates = classroomCoordinates;
        this.destinationCoordinates = destinationCoordinates;
        this.userLocationCoordinates = userLocationCoordinates;
        this.obstacleCoordinates = obstacleCoordinates;
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
        }
    }

    @NonNull
    @Override
    public ButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_button, parent, false);
        return new ButtonViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return buttonLabels.size();
    }

    // Rest of the code remains the same

    @Override
    public void onBindViewHolder(@NonNull ButtonViewHolder holder, int position) {
        String label = buttonLabels.get(position);
        Pair<Integer, Integer> coordinates = classroomCoordinates.get(label);

        // Set the button label
        holder.button.setText(label);

        // Set a click listener to calculate the Manhattan distance and open the new activity
        holder.button.setOnClickListener(view -> {
            int distance = calculateManhattanDistance(coordinates);

            // Launch the new activity and pass the distance value as an extra
            Intent intent = new Intent(view.getContext(), DistanceActivity.class);
            intent.putExtra("distance", distance); // Replace "distance" with the key you want to use to pass the distance value
            view.getContext().startActivity(intent);
        });
    }




    //맨하탄 거리 계산. 장애들 좌표를 넣으면 그 좌표는 피해서 계산
    private int calculateManhattanDistance(Pair<Integer, Integer> coordinates) {
        int classroomX = coordinates.first;
        int classroomY = coordinates.second;

        int destinationX = destinationCoordinates.first;
        int destinationY = destinationCoordinates.second;

        int userX = userLocationCoordinates.first;
        int userY = userLocationCoordinates.second;

        // Check if the classroom coordinate is an obstacle
        if (isCoordinateObstacle(classroomX, classroomY)) {
            return -1; // Return a value indicating that the classroom is not accessible
        }

        // Check if the destination coordinate is an obstacle
        if (isCoordinateObstacle(destinationX, destinationY)) {
            return -1; // Return a value indicating that the destination is not accessible
        }

        // Check if the user's location coordinate is an obstacle
        if (isCoordinateObstacle(userX, userY)) {
            return -1; // Return a value indicating that the user's location is not accessible
        }

        // Calculate the Manhattan Distance between destination and user's location
        int destinationDistance = Math.abs(destinationX - classroomX) + Math.abs(destinationY - classroomY);
        // Calculate the Manhattan Distance between user's location and classroom
        int userDistance = Math.abs(userX - classroomX) + Math.abs(userY - classroomY);

        // Return the total Manhattan Distance
        return destinationDistance + userDistance;
    }

    private boolean isCoordinateObstacle(int x, int y) { //장애물인지 아닌지 판별
        // Check if the coordinate is an obstacle
        // You can implement your logic here to determine if the coordinate is an obstacle
        // For example, check if the coordinate exists in the list of obstacle coordinates
        return obstacleCoordinates.contains(new Pair<>(x, y));
    }


    // Rest of the code remains the same
}
