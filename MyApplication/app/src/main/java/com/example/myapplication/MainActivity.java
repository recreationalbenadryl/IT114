package com.example.myapplication;

// Image URL: https://web.njit.edu/~efc27/mypic.jpg
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.drawee.view.SimpleDraweeView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Called when the "Display Image" button is pressed (android:onClick)
    public void displayImage(View view) {
        EditText editFile = findViewById(R.id.edit_file);
        String imageUrl = editFile.getText().toString().trim();

        // use Fresco to download and display the image in the Drawee
        Uri uri = Uri.parse(imageUrl);
        SimpleDraweeView draweeView = findViewById(R.id.image_area);
        draweeView.setImageURI(uri);
    }
}