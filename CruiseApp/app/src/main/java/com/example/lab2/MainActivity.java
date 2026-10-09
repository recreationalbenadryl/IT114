package com.example.lab2;   // change to match your project's package name

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Thread policy lines (see App File Input handout)
        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);
    }

    // Called when the "Display Cruises" button is pressed (android:onClick)
    public void displayCruises(View view) {
        EditText editFile = findViewById(R.id.edit_file);
        TextView textMain = findViewById(R.id.text_main);

        String address = editFile.getText().toString().trim();

        // (a) Three separate Cruise references
        Cruise cruise1, cruise2, cruise3;

        // (b) Read the web file: 6 lines per cruise, 18 lines total
        try {
            URL url = new URL(address);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(url.openStream()));

            cruise1 = readCruise(in);
            cruise2 = readCruise(in);
            cruise3 = readCruise(in);

            in.close();
        } catch (Exception e) {
            textMain.setText("Could not read file: " + e.getMessage());
            return;
        }


        StringBuilder sb = new StringBuilder();
        appendCruise(sb, 1, cruise1);
        appendCruise(sb, 2, cruise2);
        appendCruise(sb, 3, cruise3);


        int totalDays = cruise1.getLength() + cruise2.getLength() + cruise3.getLength();
        double avgLength = totalDays / 3.0;
        sb.append(String.format(Locale.US, "Average length: %.1f days\n\n", avgLength));


        double totalPrice = cruise1.getPrice() + cruise2.getPrice() + cruise3.getPrice();
        double avgPerDay = totalPrice / totalDays;
        sb.append(String.format(Locale.US, "Average price per day: $%.2f\n", avgPerDay));

        textMain.setText(sb.toString());
    }


    private Cruise readCruise(BufferedReader in) throws Exception {
        String line = in.readLine().trim();
        String ship = in.readLine().trim();
        String code = in.readLine().trim();
        String region = in.readLine().trim();
        int length = Integer.parseInt(in.readLine().trim());
        double price = Double.parseDouble(in.readLine().trim());
        return new Cruise(line, ship, code, region, length, price);
    }

    private void appendCruise(StringBuilder sb, int n, Cruise c) {
        sb.append("Cruise ").append(n).append("\n");
        sb.append("Cruise line: ").append(c.getCruiseLine()).append("\n");
        sb.append("Ship: ").append(c.getShipName()).append("\n");
        sb.append("Code: ").append(c.getCode()).append("\n");
        sb.append("Region: ").append(c.getRegion()).append("\n");
        sb.append("Length: ").append(c.getLength()).append(" days\n");
        sb.append(String.format(Locale.US, "Price: $%.2f\n\n", c.getPrice()));
    }
}