package com.example.myapplication;
// Data file URL: https://web.njit.edu/~efc27/wst_cruise_fleet_data.txt
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private interface InputHandler {
        void onInput(String text);
    }

    private final ItemList itemList = ItemList.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TextView outputView;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        outputView = findViewById(R.id.outputView);
        rootView = findViewById(R.id.rootLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_display) {
            displayList();
            return true;
        } else if (id == R.id.action_load_web) {
            promptInput("Load list from web file", "Enter file URL",
                    InputType.TYPE_TEXT_VARIATION_URI, this::loadFromWeb);
            return true;
        } else if (id == R.id.action_add) {
            promptInput("Add a new cruise", "Enter assets file name",
                    InputType.TYPE_CLASS_TEXT, this::addFromAssets);
            return true;
        } else if (id == R.id.action_details) {
            promptInput("Show cruise details", "Enter cruise code #",
                    InputType.TYPE_CLASS_TEXT, this::showDetails);
            return true;
        } else if (id == R.id.action_remove) {
            promptInput("Remove a cruise", "Enter cruise code #",
                    InputType.TYPE_CLASS_TEXT, this::removeCruise);
            return true;
        } else if (id == R.id.action_most_expensive) {
            showMostExpensive();
            return true;
        } else if (id == R.id.action_avg_length) {
            showAverageLength();
            return true;
        } else if (id == R.id.action_avg_per_day) {
            showAveragePerDay();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    private void promptInput(String title, String hint, int inputType, InputHandler handler) {
        final EditText input = new EditText(this);
        input.setInputType(inputType);
        input.setHint(hint);
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("OK", (dialog, which) -> {
                    String text = input.getText().toString().trim();
                    if (text.isEmpty()) {
                        showMessage("No input was entered.");
                    } else {
                        handler.onInput(text);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void displayList() {
        if (itemList.isEmpty()) {
            outputView.setText("");
            showMessage("The list is empty.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Cruise c : itemList.getCruises()) {
            sb.append(String.format(Locale.US, "%s, %s, %s, $%.2f%n",
                    c.getCruiseLine(), c.getShipName(), c.getCode(), c.getPrice()));
        }
        outputView.setText(sb.toString());
    }

    private void loadFromWeb(String urlString) {
        executor.execute(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                int status = connection.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> showMessage("File does not exist at that address."));
                    return;
                }
                List<Cruise> loaded = new ArrayList<>();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    Cruise c;
                    while ((c = readCruise(reader)) != null) {
                        loaded.add(c);
                    }
                }
                runOnUiThread(() -> {
                    itemList.replaceAll(loaded);
                    outputView.setText("");
                    showMessage("Loaded " + loaded.size() + " cruises.");
                });
            } catch (FileNotFoundException e) {
                runOnUiThread(() -> showMessage("File does not exist at that address."));
            } catch (IllegalArgumentException e) {
                runOnUiThread(() -> showMessage("The file data is not in the correct format."));
            } catch (IOException e) {
                runOnUiThread(() -> showMessage("Could not read the file. Check the URL and your connection."));
            } catch (Exception e) {
                runOnUiThread(() -> showMessage("Could not load the file. Check the URL and app permissions."));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void addFromAssets(String fileName) {
        try (InputStream is = getAssets().open(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            Cruise c = readCruise(reader);
            if (c == null) {
                showMessage("The assets file does not contain a complete cruise.");
                return;
            }
            if (itemList.findByCode(c.getCode()) != null) {
                showMessage("A cruise with code " + c.getCode() + " is already on the list.");
                return;
            }
            itemList.add(c);
            showMessage("Cruise " + c.getCode() + " added.");
        } catch (FileNotFoundException e) {
            showMessage("Assets file does not exist.");
        } catch (IllegalArgumentException e) {
            showMessage("The file data is not in the correct format.");
        } catch (IOException e) {
            showMessage("Assets file does not exist or could not be read.");
        }
    }

    private Cruise readCruise(BufferedReader reader) throws IOException {
        String cruiseLine = nextLine(reader);
        if (cruiseLine == null) {
            return null;
        }
        String shipName = nextLine(reader);
        String code = nextLine(reader);
        String region = nextLine(reader);
        String length = nextLine(reader);
        String price = nextLine(reader);
        String gratuity = nextLine(reader);
        if (shipName == null || code == null || region == null
                || length == null || price == null || gratuity == null) {
            throw new IllegalArgumentException("Incomplete cruise");
        }
        int days = Integer.parseInt(length);
        if (days <= 0) {
            throw new IllegalArgumentException("Invalid length");
        }
        return new Cruise(cruiseLine, shipName, code, region, days,
                Double.parseDouble(price), Double.parseDouble(gratuity));
    }

    private String nextLine(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty()) {
                return line;
            }
        }
        return null;
    }

    private void showDetails(String code) {
        Cruise c = itemList.findByCode(code);
        if (c == null) {
            showMessage("No cruise with code " + code + " was found.");
            return;
        }
        String text = String.format(Locale.US,
                "Cruise Line: %s%nShip Name: %s%nRegion: %s%nLength: %d days%nPrice: $%.2f%nTotal Gratuity: $%.2f%nTotal Cost: $%.2f",
                c.getCruiseLine(), c.getShipName(), c.getRegion(), c.getLength(),
                c.getPrice(), c.getTotalGratuity(), c.getTotalCost());
        outputView.setText(text);
    }

    private void removeCruise(String code) {
        if (itemList.removeByCode(code)) {
            outputView.setText("");
            showMessage("Cruise " + code + " removed.");
        } else {
            showMessage("No cruise with code " + code + " was found.");
        }
    }

    private void showMostExpensive() {
        if (itemList.isEmpty()) {
            showMessage("The list is empty.");
            return;
        }
        Cruise best = null;
        for (Cruise c : itemList.getCruises()) {
            if (best == null || c.getPerDayCost() > best.getPerDayCost()) {
                best = c;
            }
        }
        outputView.setText(String.format(Locale.US, "%s, %s, %s, $%.2f per day",
                best.getCruiseLine(), best.getShipName(), best.getCode(), best.getPerDayCost()));
    }

    private void showAverageLength() {
        if (itemList.isEmpty()) {
            showMessage("The list is empty.");
            return;
        }
        double total = 0;
        for (Cruise c : itemList.getCruises()) {
            total += c.getLength();
        }
        outputView.setText(String.format(Locale.US, "Average cruise length: %.1f days",
                total / itemList.size()));
    }

    private void showAveragePerDay() {
        if (itemList.isEmpty()) {
            showMessage("The list is empty.");
            return;
        }
        double totalPrice = 0;
        double totalDays = 0;
        for (Cruise c : itemList.getCruises()) {
            totalPrice += c.getPrice();
            totalDays += c.getLength();
        }
        outputView.setText(String.format(Locale.US, "Average price per day: $%.2f",
                totalPrice / totalDays));
    }
}