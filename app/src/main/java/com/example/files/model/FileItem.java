package com.example.files.model;

import android.text.Spannable;

public class FileItem {

    private FileModel fileModel;
    private Spannable filename;

    public FileItem(FileModel fileModel, Spannable filename) {
        this.fileModel = fileModel;
        this.filename = filename;
    }

    public FileModel getFileModel() {
        return fileModel;
    }

    public Spannable getFilename() {
        return filename;
    }
}