package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import android.renderscript.ScriptGroup;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.notes.list.ListStatus;
import com.example.notes.list.ListSubject;
import com.example.notes.model.Status;
import com.example.notes.model.Subject;
import com.example.notes.model.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ViewTask extends AppCompatActivity {

    private TextView title_view, date_view, deadline_view, status_view, subject_view, location_view, comment_view;
    private StandardDatabaseHelper dbh;
    private String date;
    private int category_id, task_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        category_id = CONSTANTS.CACHE.getCategoryId();
        task_id = CONSTANTS.CACHE.getTaskId();

        dbh = new StandardDatabaseHelper(ViewTask.this);

        title_view = findViewById(R.id.add_task_title);
        date_view = findViewById(R.id.add_task_date);
        deadline_view = findViewById(R.id.add_task_deadline);
        status_view = findViewById(R.id.add_task_status);
        subject_view = findViewById(R.id.add_task_subject);
        location_view = findViewById(R.id.add_task_location);
        comment_view = findViewById(R.id.add_task_comment);

        title_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call_text_dialog("Имя задачи", title_view.getText().toString().trim(), title_view);
            }
        });
        date_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_select_date_dialog(date_view);
            }
        });
        deadline_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_select_date_dialog(deadline_view);
            }
        });

        status_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_select_status_dialog();
            }
        });
        subject_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_select_subject_dialog();
            }
        });
        location_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewTask.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        comment_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup layout = findViewById(R.id.add_task_comment_layout);

                EditText edit = new EditText(ViewTask.this);
                edit.setText(comment_view.getText().toString().trim());
                edit.setSelection(edit.length());

                edit.setVerticalScrollBarEnabled(true);
                edit.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT));
                edit.setInputType(EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE | EditorInfo.TYPE_TEXT_VARIATION_LONG_MESSAGE);
                edit.setSingleLine(false);
                edit.setImeActionLabel("OK", EditorInfo.IME_ACTION_DONE);
                edit.setImeOptions(EditorInfo.IME_ACTION_DONE);

                edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            String new_comment = edit.getText().toString().trim();
                            comment_view.setText(new_comment);

                            Task task = dbh.getTask(CONSTANTS.CACHE.getTaskId());
                            dbh.updateTask(
                                    String.valueOf(task.getId()), task.getName(), task.getDate(), task.getDeadline(),
                                    task.getCategory_id(), task.getSubject_id(), task.getStatus_id(),
                                    task.getLocation(), new_comment);

                            layout.removeView(edit);
                            layout.addView(comment_view);
                            comment_view.setVisibility(View.VISIBLE);
                            return true;
                        }
                        return false;
                    }
                });

                layout.removeView(comment_view);
                layout.addView(edit);
                edit.requestFocus();

                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    @Override
    protected void onResume() {
        category_id = CONSTANTS.CACHE.getCategoryId();
        task_id = CONSTANTS.CACHE.getTaskId();
        load_task();
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void load_task() {
        Task task = dbh.getTask(task_id);
        Status status = dbh.getStatus(task.getStatus_id());
        Subject subject = dbh.getSubject(task.getSubject_id());

        title_view.setText(task.getName());
        date_view.setText(task.getDate());
        deadline_view.setText(task.getDeadline());
        status_view.setText(status.getName());
        subject_view.setText(subject.getName());
        location_view.setText(task.getLocation());
        comment_view.setText(task.getComment());
    }

    private void update_task() {
        Subject subject = dbh.getSubject(subject_view.getText().toString().trim());
        if (subject == null) {
            subject = (Subject) dbh.addSubject(subject_view.getText().toString().trim());
        }

        Status status = dbh.getStatus(status_view.getText().toString().trim());
        if (status == null) {
            status = (Status) dbh.addStatus(status_view.getText().toString().trim());
        }

        String title = title_view.getText().toString().trim();
        String date_string = date_view.getText().toString().trim();
        String deadline = deadline_view.getText().toString().trim();
        String location = "Красноясрк";
        String comment = comment_view.getText().toString().trim();
        dbh.updateTask(String.valueOf(task_id), title, date_string, deadline, category_id, subject.getId(), status.getId(), location, comment);
    }

    private void show_select_date_dialog(TextView view) {
        // CALENDAR
        date = view.getText().toString().split(" ")[1];
        CalendarView cv = new CalendarView(this);
        cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "." + (month + 1) + "." + year;
            }
        });
        // TIME
        TimePicker tp = new TimePicker(this);
        tp.setIs24HourView(true);
        // LAYOUT
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(cv);
        ll.addView(tp);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор даты и времени");
        builder.setView(ll);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String time = tp.getCurrentHour().toString() + ":" + tp.getCurrentMinute().toString() + " " + date;
                view.setText(time);
                update_task();
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void show_select_status_dialog() {
        List<Status> items = dbh.getAllStatus();
        CharSequence[] values = new CharSequence[items.size()];
        for (int i = 0; i < items.size(); i++) {
            values[i] = items.get(i).getName();
        }

        final Status[] selected = {null};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор статуса");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items.size() != 0) {
                    selected[0] = items.get(which);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected[0] != null) {
                    status_view.setText(selected[0].getName());
                    update_task();
                }
            }
        });
        builder.setNeutralButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ViewTask.this, ListStatus.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void show_select_subject_dialog() {
        List<Subject> items = dbh.getAllSubject();
        CharSequence[] values = new CharSequence[items.size()];
        for (int i = 0; i < items.size(); i++) {
            values[i] = items.get(i).getName();
        }

        final Subject[] selected = {null};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор статуса");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items.size() != 0) {
                    selected[0] = items.get(which);
                }
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selected[0] != null) {
                    subject_view.setText(selected[0].getName());
                    update_task();
                }
            }
        });
        builder.setNeutralButton("Изменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(ViewTask.this, ListSubject.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void call_text_dialog(String title, String text, TextView view) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText(text);
        edit.setSelection(edit.length());

        dialog.setTitle(title);
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                view.setText(edit.getText().toString().trim());
                update_task();
            }
        });
        dialog.show();
    }
}