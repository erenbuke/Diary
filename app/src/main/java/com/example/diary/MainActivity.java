package com.example.diary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView entryText;
    List<CardView> views = new ArrayList<>();
    List<CardView> selected = new ArrayList<>();
    LinearLayout linearLayout;
    MenuItem delete;

    int i = 0;
    boolean selectMode = false;
    public static List<Entry> Entries = new ArrayList<>();
    private final String FILE_PATH = "entries.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        entryText = (TextView) findViewById(R.id.no_entries);
        linearLayout = (LinearLayout) findViewById(R.id.linear);

        load();

        setSupportActionBar(toolbar);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
        }

        if(Entries.size() == 0){
            entryText.setVisibility(View.VISIBLE);
        }
        else{
            entryText.setVisibility(View.INVISIBLE);

            for (Entry entry: Entries){
                createCardView(entry, i);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        save();
    }

    @Override
    protected void onStop() {
        super.onStop();

        save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        delete = menu.findItem(R.id.delete_entry);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.add_entry){

            Intent intent = new Intent(this, AddEntryActivity.class);
            startActivityForResult(intent, 1);
            //Toast.makeText(this, "Add Button", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.delete_entry) {

            //Toast.makeText(this, "Delete Button", Toast.LENGTH_SHORT).show();
            deleteConfirm();
            selectMode = false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED){
            Toast.makeText(this, "Cannot proceed without read and write permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            save();

            if(Entries.size() == 0){
                entryText.setVisibility(View.VISIBLE);
            }
            else{
                entryText.setVisibility(View.INVISIBLE);
                createCardView(Entries.get(Entries.size() - 1), Entries.size() - 1);
                Toast.makeText(this, "New Entry Added", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void save(){
        String text = Entry.changeString(MainActivity.Entries);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = openFileOutput(FILE_PATH, MODE_PRIVATE);
            fileOutputStream.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        FileInputStream inputStream = null;

        try {
            inputStream = openFileInput(FILE_PATH);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader buffer = new BufferedReader(reader);
            StringBuilder builder = new StringBuilder();
            String text;
            String[] splittext;

            while((text = buffer.readLine()) != null){
                builder.append(text).append("\n");

                if(!builder.toString().equals("\n")){
                    splittext = builder.toString().split("#");

                    Entry entry = new Entry(splittext[0], splittext[2], splittext[3], splittext[4], splittext[1]);
                    entry.setImagePath(splittext[5].substring(0, splittext[5].length() - 1));

                    Entries.add(entry);
                }
                builder = new StringBuilder();
            }

            Log.d("path", builder.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createCardView(Entry entry, int id){

        CardView cardView = new CardView(this);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
        cardView.setLayoutParams(layoutparams);
        cardView.setCardElevation(10);
        cardView.setPreventCornerOverlap(true);
        cardView.setUseCompatPadding(true);
        cardView.setRadius(20);
        cardView.setClickable(true);
        cardView.setId(id);
        cardView.setOnClickListener(v -> cardClick(cardView.getId()));
        cardView.setOnLongClickListener(v -> cardLongClick(cardView.getId()));
        i++;

        ImageView imageView = new ImageView(this);

        LinearLayout.LayoutParams layoutparams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 340);
        imageView.setLayoutParams(layoutparams2);
        imageView.setImageBitmap(BitmapFactory.decodeFile(entry.getImagePath()));

        TextView textView = new TextView(this);

        LinearLayout.LayoutParams layoutparams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
        layoutparams3.setMargins(10,0,0,0);
        textView.setLayoutParams(layoutparams3);
        textView.setGravity(Gravity.BOTTOM);
        textView.setText(entry.getName());

        cardView.addView(imageView);
        cardView.addView(textView);
        linearLayout.addView(cardView);
        views.add(cardView);

    }

    private void cardClick(int id){
        if(selectMode){
            if(selected.contains(views.get(id))){
                views.get(id).setBackgroundColor(Color.WHITE);
                selected.remove(views.get(id));
            }
            else{
                views.get(id).setBackgroundColor(Color.YELLOW);
                selected.add(views.get(id));
            }

            if(selected.size() == 0){
                selectMode = false;
                delete.setVisible(false);
            }
        }
        else{

            if(!Entries.get(id).getPassword().equals(" ") && !Entries.get(id).getPassword().equals("")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Please Enter The Password");

                EditText input = new EditText(this);

                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();

                        if(m_Text.equals(Entries.get(id).getPassword())){
                            Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
                            intent.putExtra("id", id);

                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();

                            dialog.cancel();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
            else{
                Intent intent = new Intent(MainActivity.this, ViewEntryActivity.class);
                intent.putExtra("id", id);

                startActivity(intent);
            }


            //open another activity

        }
    }

    private boolean cardLongClick(int id){
        selectMode = true;
        delete.setVisible(true);

        if(selected.contains(views.get(id))){
            views.get(id).setBackgroundColor(Color.WHITE);
            selected.remove(views.get(id));
        }
        else{
            views.get(id).setBackgroundColor(Color.YELLOW);
            selected.add(views.get(id));
        }

        if(selected.size() == 0){
            selectMode = false;
            delete.setVisible(false);
        }

        return true;
    }

    private void deleteConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClick).setNegativeButton("No", dialogClick).show();
    }

    DialogInterface.OnClickListener dialogClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i){
                case DialogInterface.BUTTON_POSITIVE:
                    for (CardView select : selected){
                        Entries.remove(select.getId());
                        ((ViewManager) select.getParent()).removeView(select);
                    }

                    selected.clear();

                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialogInterface.dismiss();
                    break;
            }


        }
    };

}