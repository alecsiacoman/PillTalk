package com.example.customnotificationapp.Repository;

import com.example.customnotificationapp.Model.Medication;

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
