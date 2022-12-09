package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
        DBHandler db = new DBHandler(getApplicationContext());
        Toast.makeText(getApplicationContext(), "All notes and tasks has been exported", Toast.LENGTH_SHORT).show();
    }
}