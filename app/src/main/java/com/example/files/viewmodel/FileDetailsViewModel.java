package com.example.files.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.files.model.FileAttributes;
import com.example.files.model.FileModel;
import com.example.files.util.Utils;

import java.io.File;

public class FileDetailsViewModel extends AndroidViewModel {

    private MutableLiveData<Bitmap> filePreviewBitmap = new MutableLiveData<>();
    private MutableLiveData<String> fileSize = new MutableLiveData<>();
    private MutableLiveData<String> fileCreationTime = new MutableLiveData<>();
    private MutableLiveData<String> fileLastModifiedTime = new MutableLiveData<>();
    private MutableLiveData<String> filePath = new MutableLiveData<>();

    public FileDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Bitmap> filePreviewBitmap() {
        return filePreviewBitmap;
    }

    public LiveData<String> fileSize() {
        return fileSize;
    }

    public LiveData<String> fileCreationTime() {
        return fileCreationTime;
    }

    public LiveData<String> fileLastModifiedTime() {
        return fileLastModifiedTime;
    }

    public LiveData<String> filePath() {
        return filePath;
    }

    public void setFile(FileModel fileModel) {
        if (fileModel == null) {
            return;
        }

        /*
         * Glide should be used as an external dependency.
         */
        Glide.with(getApplication())
                .asBitmap()
                .load(new File(fileModel.getPath()))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        filePreviewBitmap.postValue(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) { }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        filePreviewBitmap.postValue(null);
                    }
                });

        FileAttributes attrs = fileModel.getAttributes();

        fileSize.setValue(Utils.getFileSize(attrs.getSize()));
        fileCreationTime.setValue(Utils.formatDateTime(attrs.getCreationTime()));
        fileLastModifiedTime.setValue(Utils.formatDateTime(attrs.getLastModifiedTime()));
        filePath.setValue(fileModel.getPath());
    }
}