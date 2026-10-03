package com.example.lab;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import android.util.TypedValue;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TypedValue tv = new TypedValue();
        int barHeight = 0;
        if (getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
            barHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
        }
        final int appBarHeight = barHeight;

        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top + appBarHeight, bars.right, bars.bottom);
            return insets;
        });
    }

    public void processPress(View view)
            throws java.io.IOException
    {
        EditText inEdit = findViewById(R.id.edit_infile);
        EditText outEdit = findViewById(R.id.edit_outfile);
        TextView display = findViewById(R.id.text_main);

        String inName = inEdit.getText().toString().trim();
        String outName = outEdit.getText().toString().trim();

        if (inName.isEmpty() || outName.isEmpty()) {
            display.setText("Please enter both an input and an output file name.");
            return;
        }


        double[] a = new double[10];
        int num_vals = 0;
        try (InputStream is = getAssets().open(inName);
             Scanner sc = new Scanner(is)) {
            sc.useLocale(Locale.US);
            while (sc.hasNext()) {
                if (num_vals == a.length) {
                    a = Arrays.copyOf(a, a.length * 2);
                }
                a[num_vals] = sc.nextDouble();
                num_vals++;
            }
        } catch (FileNotFoundException e) {
            display.setText("Input file not found in assets: " + inName);
            return;
        } catch (InputMismatchException e) {
            display.setText("Input file contains a non-numeric value.");
            return;
        }


        floor_it(a, num_vals);


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num_vals; i++) {
            sb.append(String.format(Locale.US, "%.2f", a[i])).append("\n");
        }
        display.setText(sb.toString());


        File dir = getExternalFilesDir(null);
        if (dir == null) {
            throw new IOException("External files directory is unavailable.");
        }
        File outFile = new File(dir, outName);
        try (PrintWriter pw = new PrintWriter(new FileWriter(outFile))) {
            for (int i = 0; i < num_vals; i++) {
                pw.println(String.format(Locale.US, "%.2f", a[i]));
            }
        }
    }
    public void floor_it(double[] a, int num_vals)
    {
        for (int i = 0; i < num_vals; i++) {
            a[i] = Math.floor(a[i]);
        }
    }
}