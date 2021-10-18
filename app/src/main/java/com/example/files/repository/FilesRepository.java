package com.example.files.repository;

import android.content.Context;
import android.net.Uri;

import com.example.files.model.FileModel;

import java.io.IOException;
import java.util.List;

public interface FilesRepository {
    List<FileModel> getAllFiles();
    String saveFile(String filename, String content);
    Uri saveDocument(
            Context context,
            byte[] byteArray,
            String fileName,
            String mimeType) throws IOException;

}