package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class FacultyActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private listUIAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);
        recyclerView = findViewById(R.id.ui_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] items = {"Create course","View course","Sign out"};
        adapter = new listUIAdapter(this, items);
        recyclerView.setAdapter(adapter);
    }
}