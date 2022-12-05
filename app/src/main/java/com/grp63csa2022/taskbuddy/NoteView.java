package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class NoteView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_view);

        EditText txtTitle = findViewById(R.id.note_view_title),
                txtContent = findViewById(R.id.note_view_content);

        Intent intent = getIntent();
        String[] details = intent.getStringArrayExtra("details");
        boolean update = details != null;
        if (update) {
            txtTitle.setText(details[1]);
            txtContent.setText(details[2]);

            LinearLayout layout = findViewById(R.id.note_main);
            Button btn_delete = new Button(getApplicationContext());
            DBHandler db = new DBHandler(getApplicationContext());

            btn_delete.setText("Delete");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER;
            btn_delete.setLayoutParams(layoutParams);
            btn_delete.setOnClickListener(v->{
                db.deleteNote(details[0]);
                Toast.makeText(this, "Successfully Deleted Note", Toast.LENGTH_SHORT).show();
                finish();
            });
            layout.addView(btn_delete);
        }
        findViewById(R.id.note_view_save).setOnClickListener(v -> {
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
        });
    }
}