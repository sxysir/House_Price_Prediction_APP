// HistoryAdapter.java
package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<PredictionEntry> historyList;

    public HistoryAdapter(List<PredictionEntry> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PredictionEntry entry = historyList.get(position);

        // Format timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestampStr = dateFormat.format(entry.getTimestamp());

        // Set text in ViewHolder views
        holder.timestampTextView.setText("Date: " + timestampStr);
        holder.inputTextView.setText("Input: " +
                entry.getCity() + ", " +
                entry.getArea() + " Aana, " +
                entry.getFloors() + " floors, " +
                entry.getBedrooms() + " bedrooms, " +
                entry.getBathrooms() + " bathrooms");
        holder.outputTextView.setText("Price: " + entry.getPrice() + " Crore");
        // Set other fields as needed
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView;
        TextView inputTextView;
        TextView outputTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            timestampTextView = itemView.findViewById(R.id.dateTextView);
            inputTextView = itemView.findViewById(R.id.inputTextView);
            outputTextView = itemView.findViewById(R.id.outputTextView);
            // Find other TextViews by ID
        }
    }
}
