package com.example.customnotificationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MedicationManager {
    private static MedicationManager instance;
    private final List<Medication> medicationList;

    public MedicationManager() {
        this.medicationList = new ArrayList<>();
    }

    public static synchronized MedicationManager getInstance(){
        if(instance == null){
            instance = new MedicationManager();
        }
        return instance;
    }

    public List<Medication> getMedicationList() {
        return medicationList;
    }

    public void addMedication(Medication med){
        medicationList.add(med);
    }


}
