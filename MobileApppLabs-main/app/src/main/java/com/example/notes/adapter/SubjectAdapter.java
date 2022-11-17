package com.example.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.R;
import com.example.notes.list.ListSubject;
import com.example.notes.model.Subject;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>{

    private final Activity activity;
    private final Context context;
    private final List<Subject> items;

    public SubjectAdapter(Activity activity, Context context, List<Subject> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        if (items == null) {
            return;
        }
        Subject item = items.get(position);

        if (holder.name != null) {
            holder.name.setText(item.getName());
        }

        if (holder.itemLayout != null) {
            holder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ((ListSubject) activity).show_update_dialog(item);
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

    public static final class SubjectViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ConstraintLayout itemLayout;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.subject_name);
            itemLayout = itemView.findViewById(R.id.subject_item_layout);
        }
    }
}
