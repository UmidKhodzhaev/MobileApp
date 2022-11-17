package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.notes.model.Category;
import com.example.notes.model.NamedEntity;
import com.example.notes.model.Status;
import com.example.notes.model.Subject;
import com.example.notes.model.Task;

import java.util.ArrayList;
import java.util.List;

public class StandardDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Notes.db";
    private static final int DATABASE_VERSION = 30;

    private final Context context;

    // DATABASE TABLES
    public static class TableCategory {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_ID = "category_id";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME +
                        " ( " + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME + " TEXT );";
    }

    public static class TableStatus {
        public static final String TABLE_NAME = "statuses";
        public static final String COLUMN_ID = "status_id";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME +
                        " ( " + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME + " TEXT );";
    }

    public static class TableSubject {
        public static final String TABLE_NAME = "subjects";
        public static final String COLUMN_ID = "subject_id";
        public static final String COLUMN_NAME = "name";

        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME +
                        " ( " + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_NAME + " TEXT );";
    }

    public static class TableTask {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_ID = "task_id";
        public static final String COLUMN_CATEGORY = "category_id";
        public static final String COLUMN_SUBJECT = "subject_id";
        public static final String COLUMN_STATUS = "status_id";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DEADLINE = "deadline";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_COMMENT = "comment";

        public static final String CREATE_QUERY =
                "CREATE TABLE IF NOT EXISTS  " + TABLE_NAME +
                        " ( " + COLUMN_ID + " INTEGER PRIMARY KEY NOT NULL, " +
                        COLUMN_CATEGORY + " INTEGER, " +
                        COLUMN_SUBJECT + " INTEGER, " +
                        COLUMN_STATUS + " INTEGER, " +
                        COLUMN_TITLE + " TEXT, " +
                        COLUMN_DATE + " TEXT, " +
                        COLUMN_DEADLINE + " TEXT, " +
                        COLUMN_LOCATION + " TEXT, " +
                        COLUMN_COMMENT + " TEXT );";
    }

    // CONSTRUCTOR
    public StandardDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // IMPLEMENTS SQLiteOpenHelper METHODS
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableCategory.CREATE_QUERY);
        db.execSQL(TableStatus.CREATE_QUERY);
        db.execSQL(TableSubject.CREATE_QUERY);
        db.execSQL(TableTask.CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableCategory.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableStatus.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableSubject.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TableTask.TABLE_NAME);
        onCreate(db);
    }

    // GET ONE
    public Category getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Category item = null;
        String name = null;

        if (db != null) {
            Cursor cursor = db.query(
                    TableCategory.TABLE_NAME, null,
                    TableCategory.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Категория не найдена", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                name = cursor.getString(cursor.getColumnIndexOrThrow(TableCategory.COLUMN_NAME));
                item = new Category(id, name);
            }
            cursor.close();
        }
        return item;
    }

    public Category getCategory(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Category item = null;
        int id = -1;

        if (db != null) {
            Cursor cursor = db.query(
                    TableCategory.TABLE_NAME, null,
                    TableCategory.COLUMN_NAME + "=?", new String[]{String.valueOf(name)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Категория не найдена", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                id = cursor.getInt(cursor.getColumnIndexOrThrow(TableCategory.COLUMN_ID));
                item = new Category(id, name);
            }
            cursor.close();
        }
        return item;
    }

    public Status getStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Status item = null;

        if (db != null) {
            Cursor cursor = db.query(
                    TableStatus.TABLE_NAME, null,
                    TableStatus.COLUMN_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Статус " + id + " не найден", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(TableStatus.COLUMN_NAME));
                item = new Status(id, name);
            }
            cursor.close();

        }
        return item;
    }

    public Status getStatus(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Status item = null;
        int id = -1;

        if (db != null) {
            Cursor cursor = db.query(
                    TableStatus.TABLE_NAME, null,
                    TableStatus.COLUMN_NAME + "=?", new String[]{name},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Статус " + name + " не найден", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                id = cursor.getInt(cursor.getColumnIndexOrThrow(TableStatus.COLUMN_ID));
                item = new Status(id, name);
            }
            cursor.close();
        }
        return item;
    }

    public Subject getSubject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject item = null;
        String name = null;

        if (db != null) {
            Cursor cursor = db.query(
                    TableSubject.TABLE_NAME, null,
                    TableSubject.COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Предмет не найден", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                name = cursor.getString(cursor.getColumnIndexOrThrow(TableSubject.COLUMN_NAME));
                item = new Subject(id, name);
            }
            cursor.close();

        }
        return item;
    }

    public Subject getSubject(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Subject item = null;
        int id = -1;

        if (db != null) {
            Cursor cursor = db.query(
                    TableSubject.TABLE_NAME, null,
                    TableSubject.COLUMN_NAME + "=?", new String[]{String.valueOf(name)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Предмет не найден", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                id = cursor.getInt(cursor.getColumnIndexOrThrow(TableSubject.COLUMN_ID));
                item = new Subject(id, name);
            }
            cursor.close();
        }
        return item;
    }

    public Task getTask(int task_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Task item = null;

        if (db != null) {
            Cursor cursor = db.query(
                    TableTask.TABLE_NAME, null,
                    TableTask.COLUMN_ID + "=?", new String[]{String.valueOf(task_id)},
                    null, null, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Задача не найдена", Toast.LENGTH_SHORT).show();
            } else {
                cursor.moveToNext();
                int category_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_CATEGORY));
                int subject_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_SUBJECT));
                int status_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_STATUS));

                String title = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_DATE));
                String deadline = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_DEADLINE));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_LOCATION));
                String comment = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_COMMENT));

                item = new Task(task_id, subject_id, status_id, category_id, title, date, deadline, location, comment);
            }
            cursor.close();

        }
        return item;
    }

    // ADD ONE
    public NamedEntity addCategory(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableCategory.COLUMN_NAME, name);
        long result = db.insert(TableCategory.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Категория не добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Категория добавлена", Toast.LENGTH_SHORT).show();
            return new Category((int) result, name);
        }
        return null;
    }

    public NamedEntity addStatus(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableStatus.COLUMN_NAME, name);
        long result = db.insert(TableStatus.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Статус не добавлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Статус добавлен", Toast.LENGTH_SHORT).show();
            return getStatus(name);
        }
        return null;
    }

    public NamedEntity addSubject(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableSubject.COLUMN_NAME, name);
        long result = db.insert(TableSubject.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Предмет не добавлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Предмет добавлен", Toast.LENGTH_SHORT).show();
            return new Subject((int) result, name);
        }
        return null;
    }

    public Task addTask(int category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        List<Subject> subjects = getAllSubject();
        List<Status> statuses = getAllStatus();
        int subject_id = subjects.get(0).getId();
        int status_id = statuses.get(0).getId();
        String title = "Новая задача";
        String date = "00:00 01.01.2022";
        String deadline = "00:00 01.01.2022";
        String location = "";
        String comment = "";

        cv.put(TableTask.COLUMN_CATEGORY, category_id);
        cv.put(TableTask.COLUMN_SUBJECT, subject_id);
        cv.put(TableTask.COLUMN_STATUS, status_id);
        cv.put(TableTask.COLUMN_TITLE, title);
        cv.put(TableTask.COLUMN_DATE, date);
        cv.put(TableTask.COLUMN_DEADLINE, deadline);
        cv.put(TableTask.COLUMN_LOCATION, location);
        cv.put(TableTask.COLUMN_COMMENT, comment);
        long result = db.insert(TableTask.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Задача не добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Задача добавлена", Toast.LENGTH_SHORT).show();
            return new Task((int) result, subject_id, status_id, category_id, title, date, deadline, location, comment);
        }
        return null;
    }

    public Task addTask(int category_id, int subject_id, int status_id, String title, String date, String deadline, String location, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TableTask.COLUMN_CATEGORY, category_id);
        cv.put(TableTask.COLUMN_SUBJECT, subject_id);
        cv.put(TableTask.COLUMN_STATUS, status_id);
        cv.put(TableTask.COLUMN_TITLE, title);
        cv.put(TableTask.COLUMN_DATE, date);
        cv.put(TableTask.COLUMN_DEADLINE, deadline);
        cv.put(TableTask.COLUMN_LOCATION, location);
        cv.put(TableTask.COLUMN_COMMENT, comment);
        long result = db.insert(TableTask.TABLE_NAME, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Задача не добавлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Задача добавлена", Toast.LENGTH_SHORT).show();
            return new Task((int) result, subject_id, status_id, category_id, title, date, deadline, location, comment);
        }
        return null;
    }

    // SELECT * FROM TABLE
    public List<Category> getAllCategory() {
        String query = "SELECT * FROM " + TableCategory.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Category> items = null;

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Нет категорий", Toast.LENGTH_SHORT).show();
            } else {
                items = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableCategory.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(TableCategory.COLUMN_NAME));
                    items.add(new Category(id, name));
                }
            }
            cursor.close();
        }
        return items;
    }

    public List<Status> getAllStatus() {
        String query = "SELECT * FROM " + TableStatus.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Status> items = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Нет статусов", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableStatus.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(TableStatus.COLUMN_NAME));
                    items.add(new Status(id, name));
                }
            }
            cursor.close();
        }
        if (items.size() == 0) {
            items.add((Status) addStatus("Новый статус"));
        }
        return items;
    }

    public List<Subject> getAllSubject() {
        String query = "SELECT * FROM " + TableSubject.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Subject> items = new ArrayList<>();

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Нет предметов", Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableSubject.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(TableSubject.COLUMN_NAME));
                    items.add(new Subject(id, name));
                }
            }
            cursor.close();
        }
        if (items.size() == 0) {
            items.add((Subject) addSubject("Новый предмет"));
        }
        return items;
    }

    public List<Task> getAllTask(int where_category_id) {
        String query = "SELECT * FROM " + TableTask.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        List<Task> items = null;

        if (db != null) {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                Toast.makeText(context, "Нет задач", Toast.LENGTH_SHORT).show();
            } else {
                items = new ArrayList<>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_ID));
                    int category_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_CATEGORY));
                    int subject_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_SUBJECT));
                    int status_id = cursor.getInt(cursor.getColumnIndexOrThrow(TableTask.COLUMN_STATUS));

                    String title = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_TITLE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_DATE));
                    String deadline = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_DEADLINE));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_LOCATION));
                    String comment = cursor.getString(cursor.getColumnIndexOrThrow(TableTask.COLUMN_COMMENT));

                    if (category_id == where_category_id) {
                        items.add(new Task(id, subject_id, status_id, category_id, title, date, deadline, location, comment));
                    }
                }
            }
            cursor.close();
        }
        return items;
    }

    //    UPDATE ONE ITEM
    public NamedEntity updateCategory(String row_id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableCategory.COLUMN_NAME, name);
        long result = db.update(TableCategory.TABLE_NAME, cv, TableCategory.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Категория не обновлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Категория обновлена", Toast.LENGTH_SHORT).show();
            return new Category((int) result, name);
        }
        return null;
    }

    public NamedEntity updateStatus(String row_id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableStatus.COLUMN_NAME, name);
        long result = db.update(TableStatus.TABLE_NAME, cv, TableStatus.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Статус не обновлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Статус обновлен", Toast.LENGTH_SHORT).show();
            return new Status((int) result, name);
        }
        return null;
    }

    public NamedEntity updateSubject(String row_id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableSubject.COLUMN_NAME, name);
        long result = db.update(TableSubject.TABLE_NAME, cv, TableSubject.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Предмет не обновлен", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Предмет обновлен", Toast.LENGTH_SHORT).show();
            return new Subject((int) result, name);
        }
        return null;
    }

    public Task updateTask(String row_id, String title, String date, String deadline, int category_id, int subject_id, int status_id, String location, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableTask.COLUMN_TITLE, title);
        cv.put(TableTask.COLUMN_CATEGORY, category_id);
        cv.put(TableTask.COLUMN_SUBJECT, subject_id);
        cv.put(TableTask.COLUMN_STATUS, status_id);

        cv.put(TableTask.COLUMN_DATE, date);
        cv.put(TableTask.COLUMN_DEADLINE, deadline);

        cv.put(TableTask.COLUMN_LOCATION, location);
        cv.put(TableTask.COLUMN_COMMENT, comment);
        long result = db.update(TableTask.TABLE_NAME, cv, TableTask.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Задача не обновлена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Задача обновлена", Toast.LENGTH_SHORT).show();
            return new Task((int) result, subject_id, status_id, category_id, title, date, deadline, location, comment);
        }
        return null;
    }

    //    DELETE ONE ITEM
    public void deleteOneCategory(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TableCategory.TABLE_NAME, TableCategory.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Категория не удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Категория удалена", Toast.LENGTH_SHORT).show();
            deleteAllTask(row_id);
        }
    }

    public void deleteOneStatus(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TableStatus.TABLE_NAME, TableStatus.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Статус не удален", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Статус удален", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneSubject(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TableSubject.TABLE_NAME, TableSubject.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Предмет не удален", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Предмет удален", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneTask(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TableTask.TABLE_NAME, TableTask.COLUMN_ID + "=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Задача не удалена", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT).show();
        }
    }

    //    DELETE ALL FROM TABLE
    public void deleteAllCategory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableCategory.TABLE_NAME);
        deleteAllTask();
    }

    public void deleteAllStatus() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableStatus.TABLE_NAME);
    }

    public void deleteAllSubject() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableSubject.TABLE_NAME);
    }

    public void deleteAllTask(String category_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableTask.TABLE_NAME + " WHERE category_id=" + category_id);
    }

    public void deleteAllTask() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TableTask.TABLE_NAME);
    }
}
