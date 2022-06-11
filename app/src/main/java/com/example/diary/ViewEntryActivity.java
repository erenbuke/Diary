package com.example.diary;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewEntryActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView entryImage;
    TextView entryName;
    TextView entryDate;
    TextView entryMood;
    TextView entryText;
    Button editButton;
    Button pdfButton;
    ConstraintLayout layout;

    int pageHeight = 1120;
    int pagewidth = 792;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entry);

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        entryImage = findViewById(R.id.view_image);
        entryName = findViewById(R.id.view_name);
        entryDate = findViewById(R.id.view_date);
        entryMood = findViewById(R.id.view_mood);
        entryText = findViewById(R.id.view_text);
        editButton = findViewById(R.id.edit_entry);
        pdfButton = findViewById(R.id.pdf);
        toolbar = findViewById(R.id.toolbar3);
        layout = findViewById(R.id.constlayout);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Entry entry = MainActivity.Entries.get(id);

        entryImage.setImageBitmap(BitmapFactory.decodeFile(entry.getImagePath()));
        entryName.setText(entry.getName());
        entryDate.setText(entry.getDate());
        entryMood.setText(entry.getMood());
        entryText.setText(entry.getMaintext());



        pdfButton.setOnClickListener(v -> createPDF());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 0);
    }

    private void createPDF(){
        Bitmap bitmap = loadBitmapFromView();
        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();

        canvas.drawBitmap(bitmap, 0,0, paint);
        document.finishPage(page);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        File file = new File(Environment.getExternalStorageDirectory(), "patates.pdf");

        try {
            FileOutputStream stream = new FileOutputStream(file);
            document.writeTo(stream);

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
    }

    public Bitmap loadBitmapFromView() {
        Bitmap b = Bitmap.createBitmap( layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        layout.draw(c);
        return b;
    }
}