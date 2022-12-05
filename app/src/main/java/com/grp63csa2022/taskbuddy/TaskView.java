package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TaskView extends AppCompatActivity {
    LinearLayout task_container;
    EditText txtTitle;
    String[] details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);
        task_container = findViewById(R.id.task_view_tasks);
        txtTitle = findViewById(R.id.task_view_title);
        findViewById(R.id.task_view_add).setOnClickListener(v -> {
            task_container.addView(
                    createTask("", false),
                    task_container.getChildCount()-1
            );
        });
        // Check for details
        Intent intent = getIntent();
        details = intent.getStringArrayExtra("details"); // returns null if not found
        if (details != null) { // IF UPDATE
            placeTasks();
        }
        findViewById(R.id.task_view_save).setOnClickListener(v -> saveTasks(details != null));
    }

    private void placeTasks(){
        /*
            0 = id
            1 = title
            2 = content (each task separated by \n)
            3 = status
         */
        // Place the title
        txtTitle.setText(details[1]);
        // Place the tasks
        String[] task = details[2].split("\n");
        String[] status = details[3].split("\n");
        for (int i = 0; i < task.length; i++) {
            System.out.println(task[i]);
            System.out.println(status[i]);
            task_container.addView(
                    createTask(task[i], status[i].equals("1")),
                    task_container.getChildCount()-1
            );
        }
    }

    private LinearLayout createTask(String text, Boolean checked) {
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setWeightSum(1);
        layout.setVerticalGravity(Gravity.CENTER);
        // create the checkbox
        CheckBox cb = new CheckBox(this);
        cb.setChecked(checked);
        // create the editText
        EditText txt = new EditText(this);
        txt.setText(text);
        txt.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1 // make it autofill the available space
        ));
        // create the delete task button
        Button btn = new Button(this);
        btn.setAllCaps(false);
        btn.setText("x");
        int btnWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                40,
                getResources().getDisplayMetrics());
        btn.setMinHeight(0);
        btn.setMinWidth(0);
        btn.setPadding(0,0,0,0);
        btn.setBackground(null);
        btn.setLayoutParams(new ViewGroup.LayoutParams(btnWidth, ViewGroup.LayoutParams.MATCH_PARENT));
        btn.setTextColor(Color.RED);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        // delete on click
        btn.setOnClickListener(v -> task_container.removeView(layout));
        // add the layouts
        layout.addView(cb);
        layout.addView(txt);
        layout.addView(btn);
        return layout;
    }

    private void saveTasks(Boolean update) {
        // Get the title
        String title = txtTitle.getText().toString(),
                tasks = "",
                status = "";
        int childCount = task_container.getChildCount()-1;
        // Get each task and status separate using "\n" until n-1
        for (int i = 0; i < childCount; i++) {
            LinearLayout child = (LinearLayout) task_container.getChildAt(i);
            /*
                0 = checkbox
                1 = edit text
                2 = delete button
             */
            CheckBox child_status = (CheckBox) child.getChildAt(0);
            EditText child_task = (EditText) child.getChildAt(1);
            tasks += child_task.getText().toString() + "\n";
            status += (child_status.isChecked() ? "1" : "0") + "\n";
        }

        // Get now the Database
        DBHandler db = new DBHandler(this);
        if (update){
            db.updateTask(details[0], title, tasks, status);
            Toast.makeText(this, "Updated Task Successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            db.insertTask(title, tasks, status);
            Toast.makeText(this, "Saved Task Successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}