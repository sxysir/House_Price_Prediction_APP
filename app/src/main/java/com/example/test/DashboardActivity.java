package com.example.test;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import com.example.test.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class DashboardActivity extends AppCompatActivity {

    private Spinner citySpinner;
    private EditText spinnerArea;
    private EditText spinnerFloors;
    private EditText spinnerBedrooms;
    private EditText spinnerBathrooms;
    private Button predictButton;
    private ProgressBar progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        citySpinner = findViewById(R.id.citySpinner);
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.city_array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        spinnerArea = findViewById(R.id.areaEditText);
        spinnerFloors = findViewById(R.id.floorsEditText);
        spinnerBedrooms = findViewById(R.id.bedroomsEditText);
        spinnerBathrooms = findViewById(R.id.bathroomsEditText);
        predictButton = findViewById(R.id.predictButton);
        progressDialog = findViewById(R.id.progressBar);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setVisibility(View.VISIBLE);

                // Check for empty values
                if (spinnerArea.getText().toString().isEmpty() || spinnerFloors.getText().toString().isEmpty()
                        || spinnerBedrooms.getText().toString().isEmpty() || spinnerBathrooms.getText().toString().isEmpty()) {
                    progressDialog.setVisibility(View.GONE);
                    Toast.makeText(DashboardActivity.this, "Please enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }

                String selectedCity = citySpinner.getSelectedItem().toString();  
                double area = Double.parseDouble(spinnerArea.getText().toString());
                float floors = Float.parseFloat(spinnerFloors.getText().toString());
                int bedrooms = Integer.parseInt(spinnerBedrooms.getText().toString());
                int bathrooms = Integer.parseInt(spinnerBathrooms.getText().toString());

                new PredictionTask(selectedCity, area, floors, bedrooms, bathrooms).execute();
            }
        });

        addValidationTextListeners();

        // Setup the View History button
        ImageButton viewHistoryButton = findViewById(R.id.historyButton);
        viewHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent historyIntent = new Intent(DashboardActivity.this, HistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        // Setup the three-dot menu
        ImageView menuIcon = findViewById(R.id.menuIcon);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

    }

    private void addValidationTextListeners() {
        spinnerArea.addTextChangedListener(new ValidationTextWatcher(spinnerArea, 3.5, 150.0));
        spinnerFloors.addTextChangedListener(new ValidationTextWatcher(spinnerFloors, 1, 20));
        spinnerBedrooms.addTextChangedListener(new ValidationTextWatcher(spinnerBedrooms, 2, 60));
        spinnerBathrooms.addTextChangedListener(new ValidationTextWatcher(spinnerBathrooms, 1, 50));
    }

    private class ValidationTextWatcher implements TextWatcher {
        private EditText editText;
        private double minValue;
        private double maxValue;

        public ValidationTextWatcher(EditText editText, double minValue, double maxValue) {
            this.editText = editText;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not needed for validation
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Not needed for validation
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String input = editable.toString();
            if (!input.isEmpty()) {
                double value = Double.parseDouble(input);
                if (value < minValue || value > maxValue) {
                    editText.setError("Invalid value. Must be between " + minValue + " and " + maxValue);
                    predictButton.setEnabled(false);
                } else {
                    editText.setError(null);
                    predictButton.setEnabled(true);
                }
            }
        }
    }

    private class PredictionTask extends AsyncTask<Void, Void, String> {
        private String selectedCity;
        private double selectedArea;
        private float selectedFloors;
        private int selectedBedrooms;
        private int selectedBathrooms;

        public PredictionTask(String city, double area, float floors, int bedrooms, int bathrooms) {
            this.selectedCity = city;
            this.selectedArea = area;
            this.selectedFloors = floors;
            this.selectedBedrooms = bedrooms;
            this.selectedBathrooms = bathrooms;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                if (selectedArea <= 0 || selectedFloors <= 0 || selectedBedrooms < 2 || selectedBathrooms < 1) {
                    return "Invalid input parameters";
                }

                JSONObject requestBodyJson = new JSONObject();
                requestBodyJson.put("city", selectedCity);  // Include city in the request
                requestBodyJson.put("area", selectedArea);
                requestBodyJson.put("floors", selectedFloors);
                requestBodyJson.put("bedroom", selectedBedrooms);
                requestBodyJson.put("bathroom", selectedBathrooms);

                String requestBody = requestBodyJson.toString();

                String apiUrl = "https://homevaluexpert-a23b27188b62.herokuapp.com/predict";
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setDoOutput(true);

                OutputStream os = urlConnection.getOutputStream();
                os.write(requestBody.getBytes());
                os.flush();
                os.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    return convertStreamToString(in);
                } else {
                    return "HTTP Error: " + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.setVisibility(View.GONE);
            double price = 0.0;  // Initialize price here

            if (result != null) {
                try {
                    // Parse JSON response
                    JSONObject jsonResponse = new JSONObject(result);
                    price = jsonResponse.getDouble("Price");

                    // Format price to show both truncated and full values
                    String resultTruncated = String.format("%.2f", price);
                    String resultFull = String.valueOf(price);

                    // Show result dialog with both truncated and full values
                    showResultDialog(resultTruncated, resultFull);

                    // Add to history
                    addToHistory(selectedCity, selectedArea, selectedFloors, selectedBedrooms, selectedBathrooms, price);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DashboardActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DashboardActivity.this, "Prediction failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String convertStreamToString(InputStream is) {
        try (Scanner scanner = new Scanner(is).useDelimiter("\\A")) {
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    private void showResultDialog(String resultTruncated, String resultFull) {
        View customDialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

        TextView titleTextView = customDialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = customDialogView.findViewById(R.id.messageTextView);
        Button okButton = customDialogView.findViewById(R.id.okButton);
        Button mapsButton = customDialogView.findViewById(R.id.mapsButton);

        titleTextView.setText("Predicted Value");
        messageTextView.setText(" \n\uD835\uDDE5\uD835\uDDEE\uD835\uDE04 \uD835\uDDE3\uD835\uDDFF\uD835\uDDF6\uD835\uDDF0\uD835\uDDF2 : " + resultFull + " \uD835\uDCD2\uD835\uDCFB\uD835\uDCF8\uD835\uDCFB\uD835\uDCEE\n\n\uD83C\uDDF3\uD83C\uDDF5\uD835\uDDEE\uD835\uDDFD\uD835\uDDFD\uD835\uDDFF\uD835\uDDFC\uD835\uDE05 \uD835\uDDE3\uD835\uDDFF\uD835\uDDF6\uD835\uDDF0\uD835\uDDF2: " + resultTruncated + " \uD835\uDDD6\uD835\uDDFF\uD835\uDDFC\uD835\uDDFF\uD835\uDDF2");

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog (if needed) and do any other actions
            }
        });

        mapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMaps();
            }
        });

        AlertDialog customDialog = new AlertDialog.Builder(DashboardActivity.this)
                .setView(customDialogView)
                .create();

        customDialog.show();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.dismiss();
                // Add any other actions you want to perform on "OK" click
            }
        });
    }

    private void openMaps() {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=Kathmandu, Nepal");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps?q=Kathmandu,+Nepal")));
        }
    }

    private void addToHistory(String city, double area, float floors, int bedrooms, int bathrooms, double price) {
        PredictionEntry entry = new PredictionEntry(city, area, floors, bedrooms, bathrooms, price);
        if (!HistoryManager.getInstance().entryExists(entry)) {
            HistoryManager.getInstance().addEntry(entry);
        }
    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.inflate(R.menu.menu_dashboard);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.infoMenuItem:

                        openUrl("https://drive.google.com/file/d/1jymnZvVjFSIz3KCZv5g-ehgETGhWGONA/view?usp=sharing");

                        return true;
                    case R.id.historyMenuItem:

                        navigateToHistory();
                        return true;
                    case R.id.tutorialMenuItem:

                        openUrl("https://youtube.com/shorts/Sjhn6xFZoNA?feature=share");

                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    private void navigateToHistory() {
        Intent historyIntent = new Intent(DashboardActivity.this, HistoryActivity.class);
        startActivity(historyIntent);
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}
