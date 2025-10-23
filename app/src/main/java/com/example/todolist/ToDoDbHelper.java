package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class ToDoDbHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "todo.db";
	private static final int DB_VERSION = 2;

	public static final String TABLE_TODO = "todos";
	public static final String COL_ID = "id";
	public static final String COL_TITLE = "title";
	public static final String COL_DESCRIPTION = "description";
	public static final String COL_COMPLETED = "completed"; // 0/1
    public static final String COL_START_DATE = "start_date"; // epoch millis nullable
    public static final String COL_END_DATE = "end_date"; // epoch millis nullable

	public ToDoDbHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String create = "CREATE TABLE " + TABLE_TODO + " (" +
				COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COL_TITLE + " TEXT NOT NULL, " +
				COL_DESCRIPTION + " TEXT, " +
				COL_COMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                COL_START_DATE + " INTEGER, " +
                COL_END_DATE + " INTEGER" +
				")";
		db.execSQL(create);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < 2) {
			// add date columns if missing
			try { db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_START_DATE + " INTEGER"); } catch (Exception ignored) {}
			try { db.execSQL("ALTER TABLE " + TABLE_TODO + " ADD COLUMN " + COL_END_DATE + " INTEGER"); } catch (Exception ignored) {}
		}
	}

	public long insertTodo(String title, String description, boolean completed, Long startDateMillis, Long endDateMillis) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COL_TITLE, title);
		cv.put(COL_DESCRIPTION, description);
		cv.put(COL_COMPLETED, completed ? 1 : 0);
		if (startDateMillis != null) cv.put(COL_START_DATE, startDateMillis);
		if (endDateMillis != null) cv.put(COL_END_DATE, endDateMillis);
		return db.insert(TABLE_TODO, null, cv);
	}

	public ArrayList<ToDoListItems> getAllTodos() {
		ArrayList<ToDoListItems> list = new ArrayList<>();
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_TODO, null, null, null, null, null, COL_ID + " ASC");
		try {
			int idxId = c.getColumnIndexOrThrow(COL_ID);
			int idxTitle = c.getColumnIndexOrThrow(COL_TITLE);
			int idxDesc = c.getColumnIndexOrThrow(COL_DESCRIPTION);
			int idxCompleted = c.getColumnIndexOrThrow(COL_COMPLETED);
            int idxStart = c.getColumnIndexOrThrow(COL_START_DATE);
            int idxEnd = c.getColumnIndexOrThrow(COL_END_DATE);
			while (c.moveToNext()) {
				int id = c.getInt(idxId);
				String title = c.getString(idxTitle);
				String desc = c.getString(idxDesc);
				boolean completed = c.getInt(idxCompleted) == 1;
                Long start = c.isNull(idxStart) ? null : c.getLong(idxStart);
                // Map legacy columns to unified dateTimeMillis (use start)
                list.add(new ToDoListItems(id, title, desc, completed, start));
			}
		} finally {
			c.close();
		}
		return list;
	}

	public int updateTodo(int id, String title, String description, boolean completed, Long startDateMillis, Long endDateMillis) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COL_TITLE, title);
		cv.put(COL_DESCRIPTION, description);
		cv.put(COL_COMPLETED, completed ? 1 : 0);
		if (startDateMillis != null) cv.put(COL_START_DATE, startDateMillis); else cv.putNull(COL_START_DATE);
		if (endDateMillis != null) cv.put(COL_END_DATE, endDateMillis); else cv.putNull(COL_END_DATE);
		return db.update(TABLE_TODO, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
	}

	public int setCompleted(int id, boolean completed) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(COL_COMPLETED, completed ? 1 : 0);
		return db.update(TABLE_TODO, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
	}

	public int deleteTodo(int id) {
		SQLiteDatabase db = getWritableDatabase();
		return db.delete(TABLE_TODO, COL_ID + "=?", new String[]{String.valueOf(id)});
	}
}


