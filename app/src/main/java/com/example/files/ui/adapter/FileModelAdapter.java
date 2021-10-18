package com.example.files.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.files.R;
import com.example.files.model.FileItem;

import java.util.Collections;
import java.util.List;

public class FileModelAdapter extends RecyclerView.Adapter<FileModelAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(FileItem fileItem);
    }

    private List<FileItem> items = Collections.emptyList();

    private OnItemClickListener listener;

    public FileModelAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<FileItem> items) {
        this.items = items;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
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

        private TextView filenameTextView;
        private TextView pathTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            filenameTextView = itemView.findViewById(R.id.filenameTextView);
            pathTextView = itemView.findViewById(R.id.pathTextView);
        }

        void bind(FileItem fileItem, OnItemClickListener listener) {
            pathTextView.setText(fileItem.getFileModel().getPath());
            filenameTextView.setText(fileItem.getFilename(), TextView.BufferType.SPANNABLE);

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onItemClick(fileItem));
            }
        }
    }
}