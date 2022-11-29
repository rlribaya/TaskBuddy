package com.grp63csa2022.taskbuddy;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "db_taskbuddy";
    private static final String TABLE_NOTES = "tbl_notes";
    private static final String TABLE_TASKS = "tbl_tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_STATUS = "status"; // ONLY FOR TASKS

    public DBHandler(Context context){
        super(context,DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTES + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_CONTENT + " TEXT" + ")"
        );
        db.execSQL(
                "CREATE TABLE " + TABLE_TASKS + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_TITLE + " TEXT,"
                        + KEY_CONTENT + " TEXT,"
                        + KEY_STATUS + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void resetDatabase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    /**
     * Gets the column names of the notes table
     * @return ID, Title, Content (specific order)
     */
    public String[] getNotesColumnNames() {
        return new String[] {
                KEY_ID,
                KEY_TITLE,
                KEY_CONTENT
        };
    }
    /**
     * Gets the column names of the tasks table
     * @return ID, Title, Content, Status (specific order)
     */
    public String[] getTasksColumnNames() {
        return new String[] {
                KEY_ID,
                KEY_TITLE,
                KEY_CONTENT,
                KEY_STATUS
        };
    }
    // **** CRUD (Create, Read, Update, Delete) Operations ***** //

    /**
     * Inserts a note into the notes table
     * @param title the title of the note
     * @param content main content of the note
     */
    public void insertNote(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, content);

        db.insert(TABLE_NOTES, null, cv);
        db.close();
    }
    /**
     * Inserts a task set into the tasks table
     * @param title the title of the task set
     * @param tasks the tasks in the set each separated by "\n"
     * @param status the status of the corresponding task each separated by "\n"
     */
    public void insertTask(String title, String tasks, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, tasks);
        cv.put(KEY_STATUS, status);

        db.insert(TABLE_TASKS, null, cv);
        db.close();
    }
    /**
     * This Method is used for getting
     * a specific note using its ID
     * @param id unique id of the note
     * @return id, title, content of specified note
     */
    @SuppressLint("Range")
    public String[] getNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.query(
                TABLE_NOTES,
                new String[]{KEY_ID, KEY_TITLE, KEY_CONTENT},
                KEY_ID+ "=?",new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null
        );
        if (!c.moveToNext()) return null; // RETURN NULL IF NOTE ID IS NOT FOUND

        String[] output =  {
                c.getString(c.getColumnIndex(KEY_ID)),
                c.getString(c.getColumnIndex(KEY_TITLE)),
                c.getString(c.getColumnIndex(KEY_CONTENT))
        };
        db.close();
        return output;
    }

    /**
     * Gets all of the notes of user.
     * To be used by a list adapter.
     * @return an ArrayList of HashMaps for list adapter use
     */
    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getAllNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " +
                KEY_ID + ", " +
                KEY_TITLE + ", " +
                KEY_CONTENT +
                " FROM " + TABLE_NOTES;
        Cursor c = db.rawQuery(q, null);
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        while (c.moveToNext()) {
            HashMap<String,String> map = new HashMap<>();
            map.put(KEY_ID, c.getString(c.getColumnIndex(KEY_ID)));
            map.put(KEY_TITLE, c.getString(c.getColumnIndex(KEY_TITLE)));
            map.put(KEY_CONTENT, c.getString(c.getColumnIndex(KEY_CONTENT)));
            list.add(map);
        }
        db.close();
        return list;
    }

    /**
     * Gets all the tasks of the user
     * To be used by a list adapter
     * @return
     */
    @SuppressLint("Range")
    public ArrayList<HashMap<String, String>> getAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        String q = "SELECT " +
                KEY_ID + ", " +
                KEY_TITLE + ", " +
                KEY_CONTENT + ", " +
                KEY_STATUS +
                " FROM " + TABLE_TASKS;
        Cursor c = db.rawQuery(q, null);
        ArrayList<HashMap<String,String>> list = new ArrayList<>();
        while (c.moveToNext()) {
            HashMap<String,String> map = new HashMap<>();
            map.put(KEY_ID, c.getString(c.getColumnIndex(KEY_ID)));
            map.put(KEY_TITLE, c.getString(c.getColumnIndex(KEY_TITLE)));
            map.put(KEY_CONTENT, c.getString(c.getColumnIndex(KEY_CONTENT)));
            map.put(KEY_STATUS, c.getString(c.getColumnIndex(KEY_STATUS)));
            list.add(map);
        }
        db.close();
        return list;
    }
    /**
     *
     * @return the number of rows in the NOTES table
     */
    public int getTotalNotes() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_NOTES,null).getCount();
        db.close();
        return result;
    }
    /**
     *
     * @return the number of rows in the TASKS table
     */
    public int getTotalTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.rawQuery("SELECT " + KEY_ID + " FROM " + TABLE_TASKS,null).getCount();
        db.close();
        return result;
    }

    /**
     * updates title & content of given id
     * @param id
     * @param title
     * @param content
     */
    public void updateNote(String id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, content);
        db.update(
                TABLE_NOTES,
                cv,
                KEY_ID + "=?",
                new String[] {id}
        );
        db.close();
    }

    /**
     * updates the title, tasks, & status of given id
     * @param id
     * @param title
     * @param tasks
     * @param status
     */
    public void updateTask(String id, String title, String tasks, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TITLE, title);
        cv.put(KEY_CONTENT, tasks);
        cv.put(KEY_STATUS, status);
        db.update(
                TABLE_TASKS,
                cv,
                KEY_ID + "=?",
                new String[] {id}
        );
        db.close();
    }
    /**
     * deletes note of given ID
     * @param id
     */
    public void deleteNote(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                TABLE_NOTES,
                KEY_ID + "=?",
                new String[]{id}
        );
        db.close();
    }
}
