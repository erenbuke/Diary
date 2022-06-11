package com.example.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.emoji2.text.EmojiMetadata;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddEntryActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText entryName;
    EditText entryPassword;
    EditText entryDate;
    Spinner entryMood;
    EditText entryText;
    Button entryAdd;
    ImageView entryImage;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Calendar calendar;
    private SimpleDateFormat format;
    private String date;
    private Entry newEntry;

    private static final String[] emojiList = new String[] {
            new String(Character.toChars(0x1F60A)),
            new String(Character.toChars(0x1F60D)),
            new String(Character.toChars(0x1F610)),
            new String(Character.toChars(0x1F634)),
            new String(Character.toChars(0x1F922)),
            new String(Character.toChars(0x1F641)),
            new String(Character.toChars(0x1F62D))};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        entryName = findViewById(R.id.entry_name);
        entryPassword = findViewById(R.id.entry_password);
        entryDate = findViewById(R.id.entry_date);
        entryMood = findViewById(R.id.entry_mood);
        entryText = findViewById(R.id.entry_text);
        entryAdd = findViewById(R.id.add_to_list);
        entryImage = findViewById(R.id.entry_image);
        newEntry = new Entry(null,null,null,null,null);

        entryDate.setInputType(InputType.TYPE_NULL);

        calendar = Calendar.getInstance();

        format = new SimpleDateFormat("dd/MM/yyyy");
        date = format.format(calendar.getTime());
        entryDate.setText(date);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, emojiList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        entryMood.setAdapter(adapter);

        entryDate.setOnClickListener(v -> showDateDialog());
        entryImage.setOnClickListener(v -> addImage());
        entryAdd.setOnClickListener(v -> addEntry());

    }

    private void addImage(){
        final CharSequence[] options = {"Camera", "From Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(options[i].equals("Camera")){

                    if(ActivityCompat.checkSelfPermission(AddEntryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddEntryActivity.this, new String[]{"android.permission.CAMERA"}, 2);
                    }

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File photo = null;
                    try {
                        photo = imageFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(photo != null){
                        Uri photoUri = FileProvider.getUriForFile(AddEntryActivity.this, "com.example.diary.fileprovider", photo);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                else if (options[i].equals("From Gallery")){
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if(options[i].equals("Cancel")){
                    dialogInterface.dismiss();
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bitmap imageBitmap = BitmapFactory.decodeFile(newEntry.getImagePath());

            entryImage.setImageBitmap(imageBitmap);
        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Uri selected = data.getData();
            String[] path = {MediaStore.Images.Media.DATA};

            Cursor c = getContentResolver().query(selected, path, null, null, null);

            c.moveToFirst();
            int index = c.getColumnIndex(path[0]);
            newEntry.setImagePath(c.getString(index));
            c.close();

            entryImage.setImageBitmap(BitmapFactory.decodeFile(newEntry.getImagePath()));

        }
    }

    private File imageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Entry_" + MainActivity.Entries.size() + "";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        newEntry.setImagePath(image.getAbsolutePath());
        return image;

    }

    private void addEntry(){
        newEntry.setDate(entryDate.getText().toString());
        newEntry.setMaintext(entryText.getText().toString());
        newEntry.setMood(entryMood.getSelectedItem().toString());
        newEntry.setName(entryName.getText().toString());
        newEntry.setPassword(entryPassword.getText().toString());

        MainActivity.Entries.add(newEntry);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void showDateDialog(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);

                entryDate.setText(format.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(AddEntryActivity.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && requestCode == 2 && grantResults[0] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "Cannot proceed without camera permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}