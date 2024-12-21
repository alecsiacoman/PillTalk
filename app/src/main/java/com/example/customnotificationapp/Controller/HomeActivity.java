package com.example.customnotificationapp.Controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.customnotificationapp.Repository.MedicationManager;
import com.example.customnotificationapp.View.MedicationAdapter;
import com.example.customnotificationapp.Model.Medication;
import com.example.customnotificationapp.R;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static HomeActivity instance;
    private List<Medication> medicationList;
    private MedicationAdapter medicationAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        instance = this;

        medicationList = MedicationManager.getInstance().getMedicationList();

        RecyclerView medicationListView = findViewById(R.id.medicationList);
        medicationAdapter = new MedicationAdapter(medicationList);
        medicationListView.setLayoutManager(new LinearLayoutManager(this));
        medicationListView.setAdapter(medicationAdapter);

        medicationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

        medicationAdapter.notifyDataSetChanged();
    }

    public void navigateToMainActivity(View view) {
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public static HomeActivity getInstance() {
        return instance;
    }

    public List<Medication> getMedicationList() {
        return medicationList;
    }

    public MedicationAdapter getMedicationAdapter() {
        return medicationAdapter;
    }
}