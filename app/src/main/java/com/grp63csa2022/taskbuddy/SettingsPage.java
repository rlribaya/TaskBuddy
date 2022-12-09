package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
        findViewById(R.id.settings_export).setOnClickListener(v -> ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                PackageManager.PERMISSION_GRANTED
        ));
        findViewById(R.id.settings_dev).setOnClickListener(v -> startActivity(new Intent(this, DevelopersPage.class)));

    }
    private void resetAll(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Reset?");
        alertDialog.setMessage("Are you sure you want to reset the application?\nAll notes and tasks will be deleted!");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", (dialogInterface, i) ->
                Toast.makeText(this, "Reset Cancelled", Toast.LENGTH_SHORT).show());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialogInterface, i) -> {
            DBHandler db = new DBHandler(getApplicationContext());
            db.resetDatabase();
            dialogConfirmation("Reset!", "App has been successfully reset");
        });
        alertDialog.show();
    }
    // For saving file after requesting permission
    @Override
    public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        exportAll();
    }
    private void exportAll(){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Export All?");
        alertDialog.setMessage("Are you sure you want to export all Notes and Tasks?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO", (dialogInterface, i) ->
                Toast.makeText(this, "Notes and Tasks has not been exported", Toast.LENGTH_SHORT).show());
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", (dialogInterface, i) -> {
            File main_folder = new File("/storage/emulated/0/Download", "/TaskBuddy");
            File notes_folder = new File(main_folder, "/notes");
            File tasks_folder = new File(main_folder, "/tasks");

            main_folder.mkdir();
            notes_folder.mkdir();
            tasks_folder.mkdir();
            try {
                DBHandler db = new DBHandler(getApplicationContext());
                // NOTES
                ArrayList<String[]> notes = db.getAllNotesArr();
                for (String[] n : notes) { // 0 = ID; 1 = TITLE; 2 = CONTENT
                    File file = new File(notes_folder, n[1] + "_note" + n[0] + ".txt");
                    FileWriter writer = new FileWriter(file);
                    writer.write(n[1] + "\n\n" + n[2]);
                    writer.close();
                }
                // TASKS
                ArrayList<String[]> tasks = db.getAllTasksArr();
                for (String[] t : tasks) { // 0 = ID; 1 = TITLE; 2 = TASK; 3 = STATUS
                    File file = new File(tasks_folder, t[1] + "_task" + t[0] + ".txt");
                    FileWriter writer = new FileWriter(file);
                    // Write title
                    writer.write(t[1] + "\n\n");
                    // Write tasks
                    String[] taskName = t[2].split("\n");
                    String[] taskStat = t[3].split("\n");
                    for (int x = 0; x < taskName.length; x++) {
                        if (taskStat[x].equals("1")) writer.write("x\t");
                        else writer.write("o\t");
                        writer.write(taskName[x] + "\n");
                    }
                    writer.close();
                }
            } catch (IOException e) {
                Toast.makeText(this, "ERROR EXPORTING", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            dialogConfirmation("Success!", "All notes and tasks has been successfully exported");
        });
        alertDialog.show();
    }
    private void dialogConfirmation(String title, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNeutralButton("OK", null);
        builder.show();
    }
}