// HistoryManager.java
package com.example.test;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static HistoryManager instance;
    private List<PredictionEntry> entries;
    private List<PredictionEntry> favorites;

    private HistoryManager() {
        entries = new ArrayList<>();
        favorites = new ArrayList<>();
    }

    public static HistoryManager getInstance() {
        if (instance == null) {
            instance = new HistoryManager();
        }
        return instance;
    }

    public void addEntry(PredictionEntry entry) {
        entries.add(entry);
    }

    public List<PredictionEntry> getEntries() {
        return entries;
    }


    public boolean entryExists(PredictionEntry entry) {
        for (PredictionEntry existingEntry : entries) {
            if (existingEntry.equals(entry)) {
                return true;
            }
        }
        return false;
    }
}
