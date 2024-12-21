package com.example.customnotificationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private List<Medication> medicationList;
    private RecyclerView medicationListView;
    private MedicationAdapter medicationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        medicationList = MedicationManager.getInstance().getMedicationList();

        medicationListView = findViewById(R.id.medicationList);
        medicationAdapter = new MedicationAdapter(medicationList);
        medicationListView.setLayoutManager(new LinearLayoutManager(this));
        medicationListView.setAdapter(medicationAdapter);

        medicationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        medicationAdapter.notifyDataSetChanged();
    }

    public void navigateToMainActivity(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}