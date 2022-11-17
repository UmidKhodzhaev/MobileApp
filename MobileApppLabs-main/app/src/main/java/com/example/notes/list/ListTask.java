package com.example.notes.list;

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

import com.example.notes.CONSTANTS;
import com.example.notes.R;
import com.example.notes.adapter.TaskAdapter;
import com.example.notes.ViewTask;
import com.example.notes.StandardDatabaseHelper;
import com.example.notes.model.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListTask extends AppCompatActivity {

    private RecyclerView recycler;
    private FloatingActionButton add_item;
    private int category_id;
    private StandardDatabaseHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_task);


        dbh = new StandardDatabaseHelper(ListTask.this);
        recycler = findViewById(R.id.taskListRecycler);
        add_item = findViewById(R.id.add_task);

        category_id = CONSTANTS.CACHE.getCategoryId();

        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int task_id = dbh.addTask(category_id).getId();
                CONSTANTS.CACHE.setTaskId(task_id);
                Intent intent = new Intent(ListTask.this, ViewTask.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        load_adapter();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_all_task) {
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить все?");
        builder.setMessage("Вы уверены, что хотите удалить все данные?");
        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                StandardDatabaseHelper dbh = new StandardDatabaseHelper(ListTask.this);
                dbh.deleteAllTask(String.valueOf(category_id));
                //Refresh Activity
                Intent intent = new Intent(ListTask.this, ListTask.class);
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

    public void load_adapter() {
        List<Task> items = dbh.getAllTask(category_id);
        TaskAdapter adapter = new TaskAdapter(ListTask.this, this, items);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(ListTask.this));
    }
}