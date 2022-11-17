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
import com.example.notes.adapter.StatusAdapter;
import com.example.notes.StandardDatabaseHelper;
import com.example.notes.model.Status;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListStatus extends AppCompatActivity {

    private StandardDatabaseHelper dbh;
    private RecyclerView recycler;
    private FloatingActionButton add_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_status);

        recycler = findViewById(R.id.status_recycler);
        add_item = findViewById(R.id.add_status);
        dbh = new StandardDatabaseHelper(ListStatus.this);

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_add_dialog();
            }
        });


        load_adapter();
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
        inflater.inflate(R.menu.status_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_status:
                confirmDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void load_adapter() {
        List<Status> items = dbh.getAllStatus();
        StatusAdapter adapter = new StatusAdapter(ListStatus.this, this, items);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(ListStatus.this));
    }

    public void show_add_dialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText("");
        edit.setSelection(edit.length());

        dialog.setTitle("Название статуса");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                dbh.addStatus(text);
                load_adapter();
            }
        });
        dialog.show();
    }

    public void show_update_dialog(Status item) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        EditText edit = new EditText(this);
        edit.setText(item.getName());
        edit.setSelection(edit.length());

        dialog.setTitle("Название статуса");
        dialog.setView(edit);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = edit.getText().toString().trim();
                dbh.updateStatus(String.valueOf(item.getId()), text);
                load_adapter();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbh.deleteOneStatus(String.valueOf(item.getId()));
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
                StandardDatabaseHelper dbh = new StandardDatabaseHelper(ListStatus.this);
                dbh.deleteAllStatus();
                //Refresh Activity
                Intent intent = new Intent(ListStatus.this, ListStatus.class);
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