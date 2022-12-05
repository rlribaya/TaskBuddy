package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Arrays;

public class TasksList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_list);
        findViewById(R.id.tasks_add).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), TaskView.class)));
        generateTasksList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateTasksList();
    }

    private void generateTasksList() {
        DBHandler db = new DBHandler(getApplicationContext());
        GridView gridView = findViewById(R.id.tasks_list);
        ListAdapter adapter = new SimpleAdapter(
                getApplicationContext(),
                db.getAllTasks(),
                R.layout.list_row,
                db.getTasksColumnNames(),
                new int[]{R.id.row_id, R.id.row_title, R.id.row_content, R.id.row_status}
        );
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            LinearLayout linearLayout = (LinearLayout) view;

            int max = linearLayout.getChildCount();
            String[] details = new String[max];
            while (max-- > 0) {
                TextView textView = (TextView) linearLayout.getChildAt(max);
                details[max] = textView.getText().toString();
            }
            Intent intent = new Intent(getApplicationContext(), TaskView.class);
            intent.putExtra("details", details);
            System.out.println(Arrays.toString(details));
            startActivity(intent);
        });
    }
}