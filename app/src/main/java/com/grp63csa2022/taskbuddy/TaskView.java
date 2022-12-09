package com.grp63csa2022.taskbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
        details = getIntent().getStringArrayExtra("details"); // returns null if not found
        placeMenu();
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
                    db.deleteTask(details[0]);
                    Toast.makeText(this, "Successfully Deleted Task", Toast.LENGTH_SHORT).show();
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
        txt.setMaxLines(1);
        txt.setInputType(InputType.TYPE_CLASS_TEXT);
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
        String title = txtTitle.getText().toString();
        String tasks[] = getTasks();
        // Get now the Database
        DBHandler db = new DBHandler(this);
        if (update){
            db.updateTask(details[0], title, tasks[0], tasks[1]);
            Toast.makeText(this, "Updated Task Successfully", Toast.LENGTH_SHORT).show();
        }
        else {
            db.insertTask(title, tasks[0], tasks[1]);
            Toast.makeText(this, "Saved Task Successfully", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
    private String[] getTasks() {
        String tasks = "", status = "";
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
        return new String[]{tasks, status};
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
    @Override // For saving file after requesting permission
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            String title = txtTitle.getText().toString();
            String id;
            if (details != null) id = details[0];
            else id = LocalDateTime.now().toString().replaceAll("[^\\d]","");

            File file = new File("/storage/emulated/0/Download", title + "_task" + id + ".txt");
            FileWriter writer = new FileWriter(file);

            String tasks[] = getTasks();
            String taskName[] = tasks[0].split("\n");
            String taskStat[] = tasks[1].split("\n");

            writer.write(title + "\n\n");
            for (int i = 0; i < taskName.length; i++) {
                if (taskStat[i].equals("1")) writer.write("x\t");
                else writer.write("o\t");
                writer.write(taskName[i] + "\n");
            }
            writer.close();
            System.out.println();
            Toast.makeText(this, "Successfully saved file at\n\"Download/"+file.getName()+"\"", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "ERROR SAVING FILE", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}