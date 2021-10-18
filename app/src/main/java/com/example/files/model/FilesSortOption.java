package com.example.files.model;

import androidx.annotation.StringRes;

import com.example.files.R;

public enum FilesSortOption {
    FILENAME_ASC(R.string.filename),
    CREATION_TIME_ASC(R.string.creation_time),
    EXTENSION_ASC(R.string.extension);

    public final int caption;

    FilesSortOption(@StringRes int caption) {
        this.caption = caption;
    }
}