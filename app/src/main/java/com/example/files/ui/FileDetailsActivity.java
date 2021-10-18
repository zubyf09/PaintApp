package com.example.files.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.files.R;
import com.example.files.model.FileModel;
import com.example.files.viewmodel.FileDetailsViewModel;

public class FileDetailsActivity extends AppCompatActivity {

    private static final String KEY_FILE_MODEL = "KEY_FILE_MODEL";

    public static void startActivity(Context context, FileModel file) {
        Intent intent = new Intent(context, FileDetailsActivity.class);
        intent.putExtra(KEY_FILE_MODEL, file);
        context.startActivity(intent);
    }

    private ImageView previewImageView;
    private TextView noPreviewTextView;
    private TextView sizeTextView;
    private TextView creationTimeTextView;
    private TextView lastModifiedTimeTextView;
    private TextView pathTextView;

    private FileDetailsViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_file_details);

        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(FileDetailsViewModel.class);

        FileModel fileModel = getIntent().getParcelableExtra(KEY_FILE_MODEL);

        if (fileModel != null) {
            setTitle(fileModel.getFilename());

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            initViews();

            observe();

            if (savedInstanceState == null) {
                viewModel.setFile(fileModel);
            }
        } else {
            Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void observe() {
        viewModel.filePreviewBitmap().observe(this, previewBitmap -> {
            if (previewBitmap != null) {
                previewImageView.setImageBitmap(previewBitmap);
                noPreviewTextView.setVisibility(View.GONE);
            } else {
                noPreviewTextView.setVisibility(View.VISIBLE);
            }
        });

        viewModel.fileSize().observe(this, size -> {
            sizeTextView.setText(size);
        });

        viewModel.fileCreationTime().observe(this, creationTime -> {
            creationTimeTextView.setText(creationTime);
        });

        viewModel.fileLastModifiedTime().observe(this, lastModifiedTime -> {
            lastModifiedTimeTextView.setText(lastModifiedTime);
        });

        viewModel.filePath().observe(this, path -> {
            pathTextView.setText(path);
        });
    }

    private void initViews() {
        previewImageView = findViewById(R.id.previewImageView);
        noPreviewTextView = findViewById(R.id.noPreviewTextView);
        sizeTextView = findViewById(R.id.sizeTextView);
        creationTimeTextView = findViewById(R.id.creationTimeTextView);
        lastModifiedTimeTextView = findViewById(R.id.lastModifiedTimeTextView);
        pathTextView = findViewById(R.id.pathTextView);
    }
}