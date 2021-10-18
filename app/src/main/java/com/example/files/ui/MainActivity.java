package com.example.files.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.files.R;
import com.example.files.model.FileItem;
import com.example.files.model.ProgressState;
import com.example.files.ui.adapter.FileModelAdapter;
import com.example.files.util.PermissionManager;
import com.example.files.util.Utils;
import com.example.files.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements PermissionManager.Listener, FileModelAdapter.OnItemClickListener {

    private static final String PM_FETCH_FILES_TAG = "PM_FETCH_FILES_TAG";
    private static final String PM_SAVE_FILES_LIST_TAG = "PM_SAVE_FILES_LIST_TAG";

    private RecyclerView filesRecycler;
    private ProgressBar progressBar;
    private FloatingActionButton saveFilesListFab;

    private FileModelAdapter filesAdapter = new FileModelAdapter(this);

    private MainViewModel viewModel;

    private PermissionManager permissionManager = new PermissionManager(this,this);

    private SortFilesDialogFragment sortDialog = new SortFilesDialogFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(MainViewModel.class);

        if (savedInstanceState == null) {
            permissionManager.requestStoragePermission(PM_FETCH_FILES_TAG);
        }

        initViews();
        setListeners();

        observe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemSort) {
            sortDialog.show(getSupportFragmentManager(), null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStoragePermissionGranted(String tag) {
        switch (tag) {
            case PM_FETCH_FILES_TAG:
                viewModel.fetchFiles();
                break;

            case PM_SAVE_FILES_LIST_TAG:
                saveFilesList();
                break;
        }
    }

    @Override
    public void onStoragePermissionDenied(String tag) {
        showStoragePermissionNotGranted(v -> permissionManager.grantStoragePermissionManually(tag));
    }

    @Override
    public void onItemClick(FileItem fileItem) {
        FileDetailsActivity.startActivity(this, fileItem.getFileModel());
    }

    private void observe() {
        viewModel.files().observe(this, files -> {
            filesAdapter.setItems(files);
        });

        viewModel.fetchFilesProgressState().observe(this, state -> {
            switch (state) {
                case IDLE:
                case DONE:
                case ERROR:
                    filesRecycler.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    break;
                case IN_PROGRESS:
                    filesRecycler.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    break;
            }
        });

        viewModel.saveFilesListProgressState().observe(this, state -> {
            saveFilesListFab.setEnabled(state != ProgressState.IN_PROGRESS);
        });

        viewModel.numberOfHighlightedFiles().observe(this, number -> {
            String text = getString(R.string.number_of_matches_d, number);

            Toast.makeText(this, text, Toast.LENGTH_LONG)
                    .show();
        });
    }

    private void saveFilesList() {
        viewModel.saveFilesList().observe(this, filePath -> {
            String text;

            if (filePath != null) {
                text = getString(R.string.file_saved_s, filePath);
            } else {
                text = getString(R.string.error_saving_file);
            }

            Toast.makeText(this, text, Toast.LENGTH_LONG)
                    .show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        permissionManager.onActivityResult(requestCode, resultCode, data);
    }

    private void showStoragePermissionNotGranted(View.OnClickListener listener) {
        View rootLayout = findViewById(R.id.rootLayout);

        Snackbar.make(rootLayout, R.string.storage_permission_not_granted, Snackbar.LENGTH_LONG)
                .setAction(R.string.grant, listener)
                .show();
    }

    private void initViews() {
        progressBar = findViewById(R.id.progressBar);

        filesRecycler = findViewById(R.id.filesRecycler);
        filesRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        filesRecycler.setAdapter(filesAdapter);

        saveFilesListFab = findViewById(R.id.saveFilesListFab);
    }

    private void setListeners() {
        EditText highlightEditText = findViewById(R.id.highlightEditText);

        highlightEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String highlight = v.getText().toString();

                viewModel.highlightFiles(highlight);

                Utils.hideSoftInput(MainActivity.this);
                return true;
            }

            return false;
        });

        saveFilesListFab.setOnClickListener(v -> {
            permissionManager.requestStoragePermission(PM_SAVE_FILES_LIST_TAG);
        });

        sortDialog.setListener(option -> {
            viewModel.sortFiles(option);

            sortDialog.dismiss();
        });
    }
}