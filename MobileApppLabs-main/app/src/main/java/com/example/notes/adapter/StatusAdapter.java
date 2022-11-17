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
import com.example.notes.list.ListStatus;
import com.example.notes.model.Status;

import java.util.List;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {

    private final Activity activity;
    private final Context context;
    private final List<Status> items;

    public StatusAdapter(Activity activity, Context context, List<Status> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public StatusAdapter.StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.status_item, parent, false);
        return new StatusAdapter.StatusViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusAdapter.StatusViewHolder holder, int position) {
        if (items == null) {
            return;
        }
        Status item = items.get(position);
        System.out.println(item.getName());

        if (holder.name != null) {
            holder.name.setText(item.getName());
        }

        if (holder.itemLayout != null) {
            holder.itemLayout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ((ListStatus) activity).show_update_dialog(item);
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

    public static final class StatusViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ConstraintLayout itemLayout;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.status_name);
            itemLayout = itemView.findViewById(R.id.status_item_layout);
        }
    }
}
