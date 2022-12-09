package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class NoteView extends AppCompatActivity {
    String details[];
    EditText txtTitle, txtContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);
        txtTitle = findViewById(R.id.note_view_title);
        txtContent = findViewById(R.id.note_view_content);

        details = getIntent().getStringArrayExtra("details");
        placeMenu();
        if (details != null) {
            txtTitle.setText(details[1]);
            txtContent.setText(details[2]);
        }
        findViewById(R.id.note_view_save).setOnClickListener(v->saveNote(details != null));
    }
    private void saveNote(boolean update){
        String title = txtTitle.getText().toString();
        String content = txtContent.getText().toString();

        DBHandler db = new DBHandler(getApplicationContext());
        if (update) {
            db.updateNote(details[0], title, content);
        } else {
            db.insertNote(title, content);
        }
        Toast.makeText(getApplicationContext(), "Saved Successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
    private void placeMenu(){
        FrameLayout frameLayout = findViewById(R.id.ribbon);
        ImageButton imgBtn = new ImageButton(this, null, 0, R.style.MoreButton);
        imgBtn.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT,
                Gravity.END
        ));
        frameLayout.addView(imgBtn);
        PopupMenu menu = new PopupMenu(this, imgBtn);
        menu.inflate(R.menu.popup_menu);
        menu.getMenu().findItem(R.id.menu_delete).setVisible(details != null);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_delete: {
                    DBHandler db = new DBHandler(getApplicationContext());
                    db.deleteNote(details[0]);
                    Toast.makeText(this, "Successfully Deleted Note", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                case R.id.menu_export: {
                    saveFile();
                }
                default: return false;
            }
            return true;
        });
        imgBtn.setOnClickListener(v -> menu.show());
    }
    // Save file to downloads but request first for permission
    private void saveFile() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED
        );
    }
    // For saving file after requesting permission
    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            String title = txtTitle.getText().toString();
            String content = txtContent.getText().toString();

            String id;
            if (details != null) id = details[0];
            else id = LocalDateTime.now().toString().replaceAll("[^\\d]","");

            File file = new File("/storage/emulated/0/Download", title + "_note" + id + ".txt");
            FileWriter writer = new FileWriter(file);
            writer.write(title + "\n\n" + content);
            writer.close();
            System.out.println();
            Toast.makeText(this, "Successfully saved file at\n\"Download/"+file.getName()+"\"", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "ERROR SAVING FILE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}