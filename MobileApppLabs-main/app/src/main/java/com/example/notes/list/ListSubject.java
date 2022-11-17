package com.example.notes.list;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.notes.CONSTANTS;
import com.example.notes.R;
import com.example.notes.adapter.SubjectAdapter;
import com.example.notes.StandardDatabaseHelper;
import com.example.notes.model.Subject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListSubject extends AppCompatActivity {

    private StandardDatabaseHelper dbh;
    private RecyclerView recycler;
    private FloatingActionButton add_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_subject);

        recycler = findViewById(R.id.subject_recycler);
        add_item = findViewById(R.id.add_subject);
        dbh = new StandardDatabaseHelper(ListSubject.this);

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_add_dialog();
            }
        });

        load_adapter();
        recycler.setLayoutManager(new LinearLayoutManager(ListSubject.this));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_subject:
                confirmDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void load_adapter() {
        List<Subject> items = dbh.getAllSubject();
        SubjectAdapter adapter = new SubjectAdapter(ListSubject.this, this, items);
        recycler.setAdapter(adapter);
    }

    public void show_add_dialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText("");
        edit.setSelection(edit.length());

        dialog.setTitle("Название предмета");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                dbh.addSubject(text);
                load_adapter();
            }
        });
        dialog.show();
    }

    public void show_update_dialog(Subject item) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText(item.getName());
        edit.setSelection(edit.length());

        dialog.setTitle("Название предмета");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                dbh.updateSubject(String.valueOf(item.getId()), text);
                load_adapter();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbh.deleteOneSubject(String.valueOf(item.getId()));
                load_adapter();
            }
        });
        dialog.show();
    }

    public void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить все?");
        builder.setMessage("Вы уверены, что хотите удалить все данные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StandardDatabaseHelper dbh = new StandardDatabaseHelper(ListSubject.this);
                dbh.deleteAllSubject();
                //Refresh Activity
                Intent intent = new Intent(ListSubject.this, ListSubject.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}