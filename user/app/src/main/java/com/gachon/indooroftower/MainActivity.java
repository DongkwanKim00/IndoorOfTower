package com.gachon.indooroftower;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.recyceler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false)) ; // 상하 스크롤
//        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)) ; // 좌우 스크롤

        adapter = new Adapter();
        for (int i = 401; i < 435; i++) {
            String str = i +"호";
            adapter.setArrayData(str);
        }

        recyclerView.setAdapter(adapter);


    }
}

//뷰 홀더 정의, 참고 블로그: https://3001ssw.tistory.com/201
//첫 메인 화면에서 버튼과 그런것들 구성하는데 필요한 클래스
//해당 호실의 버튼을 누르면 그 호실이 몇호인지에 대한 정보를 다음 activity로 넘김
class ViewHolder extends RecyclerView.ViewHolder {
    public TextView textView;
    public Button button;

    ViewHolder(final Context context, View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.textView);
        button = itemView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strText = textView.getText().toString();
                Intent intent = new Intent(context, SecondActivity.class);
                intent.putExtra("strText", strText);
                context.startActivity(intent);
            }
        });
    }
}

