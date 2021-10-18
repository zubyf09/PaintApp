package com.example.files.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.files.R;
import com.example.files.model.FilesSortOption;
import com.example.files.ui.adapter.FilesSortOptionAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;

public class SortFilesDialogFragment extends BottomSheetDialogFragment {

    private FilesSortOptionAdapter adapter = new FilesSortOptionAdapter();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_sort, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<FilesSortOption> options = Arrays.asList(FilesSortOption.values());
        adapter.setItems(options);

        RecyclerView recyclerView = view.findViewById(R.id.sortOptionsRecycler);
        recyclerView.setAdapter(adapter);
    }

    public void setListener(FilesSortOptionAdapter.OnItemClickListener listener) {
        adapter.setListener(listener);
    }
}