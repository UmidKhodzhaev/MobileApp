package com.example.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.CONSTANTS;
import com.example.notes.R;
import com.example.notes.list.ListCategory;
import com.example.notes.list.ListTask;
import com.example.notes.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Activity activity;
    private Context context;
    private List<Category> items;

    public CategoryAdapter(Activity activity, Context context, List<Category> items) {
        this.activity = activity;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        if (items == null) {
            return;
        }
        Category item = items.get(position);

        if (holder.category_title != null) {
            holder.category_title.setText(item.getName());
        }

        if (holder.item_layout != null) {
            holder.item_layout.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    ((ListCategory) activity).show_update_dialog(item);
                    return true;
                }
            });

            holder.item_layout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ListTask.class);
                    CONSTANTS.CACHE.setCategoryId(item.getId());
                    activity.startActivity(intent);
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

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView category_title;
        ConstraintLayout item_layout;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            category_title = itemView.findViewById(R.id.categoryTitle);
            item_layout = itemView.findViewById(R.id.category_item_layout);
        }
    }
}
