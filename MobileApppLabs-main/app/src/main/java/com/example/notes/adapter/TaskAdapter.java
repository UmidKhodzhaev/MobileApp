package com.example.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.CONSTANTS;
import com.example.notes.R;
import com.example.notes.ViewTask;
import com.example.notes.StandardDatabaseHelper;
import com.example.notes.list.ListTask;
import com.example.notes.model.Status;
import com.example.notes.model.Subject;
import com.example.notes.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Activity activity;
    private Context context;
    private List<Task> items;

    public TaskAdapter(Activity activity, Context context, List<Task> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View taskItems = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(taskItems);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task item = items.get(position);

        StandardDatabaseHelper dbh = new StandardDatabaseHelper(context);
        Status status = dbh.getStatus(item.getStatus_id());
        Subject subject = dbh.getSubject(item.getSubject_id());

        holder.task_title.setText(item.getName());
        holder.status.setText(status.getName());
        holder.subject.setText(subject.getName());
        holder.date.setText(item.getDate());

        if (holder.item_layout != null) {
            holder.item_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CONSTANTS.CACHE.setTaskId(item.getId());

                    Intent intent = new Intent(context, ViewTask.class);
                    activity.startActivity(intent);
                }
            });
            holder.item_layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    show_delete_dialog(item);
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public static final class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView task_title, status, subject, date;
        ConstraintLayout item_layout;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            task_title = itemView.findViewById(R.id.task_title);
            status = itemView.findViewById(R.id.status);
            subject = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);

            item_layout = itemView.findViewById(R.id.task_item_layout);
        }
    }

    public void show_delete_dialog(Task item) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();

        dialog.setTitle("Удалить '"+item.getName()+"'?");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StandardDatabaseHelper dbh = new StandardDatabaseHelper(context);
                dbh.deleteOneTask(String.valueOf(item.getId()));
                ((ListTask) activity).load_adapter();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }
}
