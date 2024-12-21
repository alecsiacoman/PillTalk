package com.example.customnotificationapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationAdapter.ViewHolder> {

    private final List<Medication> medicationList;

    public MedicationAdapter(List<Medication> medicationList) {
        this.medicationList = medicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medication_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medication medication = medicationList.get(position);

        // Format the date and time
        @SuppressLint("DefaultLocale") String formattedDate = String.format("%02d/%02d/%04d", medication.getDay(), medication.getMonth() + 1, medication.getYear());
        @SuppressLint("DefaultLocale") String formattedTime = String.format("%02d:%02d", medication.getHour(), medication.getMinute());

        holder.medNameTextView.setText(medication.getMedName());
        holder.medDateTimeTextView.setText(String.format("%s at %s", formattedDate, formattedTime));
    }

    @Override
    public int getItemCount() {
        return medicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medNameTextView;
        TextView medDateTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medNameTextView = itemView.findViewById(R.id.medNameTextView);
            medDateTimeTextView = itemView.findViewById(R.id.medDateTimeTextView);
        }
    }
}
