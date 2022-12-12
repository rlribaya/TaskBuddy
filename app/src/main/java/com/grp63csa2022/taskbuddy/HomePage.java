package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        findViewById(R.id.btn_notes).setOnClickListener(v -> startActivity(new Intent(this, NotesList.class)));
        findViewById(R.id.btn_tasks).setOnClickListener(v -> startActivity(new Intent(this, TasksList.class)));
        findViewById(R.id.btn_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsPage.class)));
        findViewById(R.id.btn_dev).setOnClickListener(v -> startActivity(new Intent(this, DevelopersPage.class)));
    }
}