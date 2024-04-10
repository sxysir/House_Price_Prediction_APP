// HistoryActivity.java
package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView historyRecyclerView;
    private HistoryAdapter historyAdapter;
    private TextView noHistoryTextView;
    private ImageButton backButton;

    private List<PredictionEntry> historyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        historyList = HistoryManager.getInstance().getEntries();

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        noHistoryTextView = findViewById(R.id.noHistoryTextView);
        backButton = findViewById(R.id.backButton);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish the activity when the back button is clicked
                finish();
            }
        });

        // Create and set the adapter
        historyAdapter = new HistoryAdapter(historyList);
        historyRecyclerView.setAdapter(historyAdapter);

        // Set layout manager to position the items
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Show/hide views based on historyList size
        updateViewVisibility();
    }

    // Helper method to show/hide views based on historyList size
    private void updateViewVisibility() {
        if (historyList.isEmpty()) {
            noHistoryTextView.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            noHistoryTextView.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
