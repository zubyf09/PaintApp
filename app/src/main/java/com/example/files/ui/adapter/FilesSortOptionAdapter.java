package com.example.files.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.files.R;
import com.example.files.model.FilesSortOption;

import java.util.Collections;
import java.util.List;

public class FilesSortOptionAdapter extends RecyclerView.Adapter<FilesSortOptionAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FilesSortOption option);
    }

    private List<FilesSortOption> items = Collections.emptyList();

    private OnItemClickListener listener;

    public void setItems(List<FilesSortOption> items) {
        this.items = items;

        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView sortOptionTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            sortOptionTextView = itemView.findViewById(R.id.sortOptionTextView);
        }

        void bind(FilesSortOption option, OnItemClickListener listener) {
            sortOptionTextView.setText(option.caption);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(option));
            }
        }
    }
}