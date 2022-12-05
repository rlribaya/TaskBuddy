package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class NotesList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        generateNotesList();
        findViewById(R.id.notes_add).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), NoteView.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateNotesList();
    }
    private void generateNotesList() {
        DBHandler db = new DBHandler(getApplicationContext());
        ListView listView = findViewById(R.id.notes_list);

        ListAdapter adapter = new SimpleAdapter(
                getApplicationContext(),
                db.getAllNotes(),
                R.layout.list_row,
                db.getNotesColumnNames(),
                new int[]{R.id.row_id, R.id.row_title, R.id.row_content}
        );
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> listOnClick(view));
    }
    private void listOnClick(View view) {
        LinearLayout linearLayout = (LinearLayout) view;

        int max = linearLayout.getChildCount();
        String[] details = new String[max];
        while (max-- > 0) {
            TextView textView = (TextView) linearLayout.getChildAt(max);
            details[max] = textView.getText().toString();
        }
        Intent intent = new Intent(getApplicationContext(), NoteView.class);
        intent.putExtra("details", details);
        System.out.println(Arrays.toString(details));
        startActivity(intent);
    }
}