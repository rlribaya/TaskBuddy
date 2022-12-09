package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        findViewById(R.id.settings_reset).setOnClickListener(v -> resetAll());
        findViewById(R.id.settings_export).setOnClickListener(v -> exportAll());
        findViewById(R.id.settings_dev).setOnClickListener(v -> startActivity(new Intent(this, DevelopersPage.class)));

    }
    private void resetAll(){
        DBHandler db = new DBHandler(getApplicationContext());
        db.resetDatabase();
        Toast.makeText(getApplicationContext(), "TaskBuddy has been reset successfully", Toast.LENGTH_SHORT).show();
    }

    private void exportAll(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED
        );
        Toast.makeText(getApplicationContext(), "All notes and tasks has been exported", Toast.LENGTH_SHORT).show();
    }
    // For saving file after requesting permission
    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        DBHandler db = new DBHandler(getApplicationContext());
        ArrayList<HashMap<String, String>> allNotes;
        allNotes = db.getAllNotes();
        ArrayList<HashMap<String, String>> allTasks;
        allTasks = db.getAllTasks();

        //iterate to all notes
        try {
            for (int i = 0; i < allNotes.size(); i++){
            HashMap<String, String> note = allNotes.get(i);


                String title = note.get("KEY_TITLE");
                String content = note.get("KEY_CONTENT");
                File file = new File("/storage/emulated/0/Download", title + "_note.txt");
                FileWriter writer = new FileWriter(file);


                writer.write(title + "\n\n" + content);

                writer.close();
                System.out.println("Successfully saved file at\n\"Download/"+file.getName()+"\"");
            }
            Toast.makeText(this, "Successfully saved file at\n\"Download/", Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Toast.makeText(this, "ERROR SAVING FILE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

}