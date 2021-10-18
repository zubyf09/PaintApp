package com.example.files.viewmodel;

import android.app.Application;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.files.R;
import com.example.files.model.FileItem;
import com.example.files.model.FileModel;
import com.example.files.model.FilesSortOption;
import com.example.files.model.ProgressState;
import com.example.files.repository.FilesRepository;
import com.example.files.repository.impl.ExternalFilesRepository;
import com.example.files.util.Utils;

import org.json.JSONArray;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainViewModel extends AndroidViewModel {

    private static final String FILES_LIST_FILENAME = "files.ptg";

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    // Should be used as an external dependency.
    private FilesRepository filesRepository = new ExternalFilesRepository();

    private MutableLiveData<List<FileItem>> files = new MutableLiveData<>();

    private MutableLiveData<ProgressState> fetchFilesProgressState = new MutableLiveData<>();
    private MutableLiveData<ProgressState> saveFilesListProgressState = new MutableLiveData<>();

    private MutableLiveData<Integer> numberOfHighlightedFiles = new MutableLiveData<>();

    private int filenameNormalColor = ContextCompat.getColor(getApplication(), R.color.colorFilenameNormal);
    private int filenameHighlightColor = ContextCompat.getColor(getApplication(), R.color.colorFilenameHighlight);

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        executorService.shutdownNow();
    }

    public LiveData<List<FileItem>> files() {
        return files;
    }

    public LiveData<ProgressState> fetchFilesProgressState() {
        return fetchFilesProgressState;
    }

    public LiveData<ProgressState> saveFilesListProgressState() {
        return saveFilesListProgressState;
    }

    public LiveData<Integer> numberOfHighlightedFiles() {
        return numberOfHighlightedFiles;
    }

    public void fetchFiles() {
        fetchFilesProgressState.setValue(ProgressState.IN_PROGRESS);

        executorService.submit(() -> {
            List<FileModel> fetchedFiles = filesRepository.getAllFiles();

            List<FileItem> fileItems = new ArrayList<>(fetchedFiles.size());

            for (FileModel fileModel : fetchedFiles) {
                String filename = fileModel.getFilename();

                fileItems.add(new FileItem(
                        fileModel,
                        createSpannable(filename, filenameNormalColor, 0, filename.length()))
                );
            }

            sortFiles(fileItems, FilesSortOption.FILENAME_ASC);
            this.files.postValue(fileItems);

            fetchFilesProgressState.postValue(ProgressState.DONE);
        });
    }

    public void highlightFiles(String filenameContains) {
        if (filenameContains == null || filenameContains.trim().isEmpty()) {
            fetchFiles();
            return;
        }

        executorService.submit(() -> {
            List<FileItem> currentFiles = files.getValue();

            int highlightedFiles = 0;

            if (currentFiles != null) {
                List<FileItem> fileItems = new ArrayList<>();

                for (FileItem fileItem : currentFiles) {
                    String filename = fileItem.getFileModel().getFilename();

                    int highlightStartIndex = filename.toLowerCase().indexOf(filenameContains.toLowerCase());
                    int highlightEndIndex = highlightStartIndex + filenameContains.length();

                    int filenameColor = filenameHighlightColor;

                    if (highlightStartIndex == -1) {
                        highlightStartIndex = 0;
                        highlightEndIndex = filename.length();

                        filenameColor = filenameNormalColor;
                    } else {
                        highlightedFiles++;
                    }

                    fileItems.add(new FileItem(
                            fileItem.getFileModel(),
                            createSpannable(filename, filenameColor, highlightStartIndex, highlightEndIndex))
                    );
                }

                this.files.postValue(fileItems);
                this.numberOfHighlightedFiles.postValue(highlightedFiles);

                final int numberOfMatches = highlightedFiles;

                triggerNumberOfMatchesNotification(numberOfMatches);
            }
        });
    }

    public LiveData<String> saveFilesList() {
        MutableLiveData<String> savedFilePath = new MutableLiveData<>();

        saveFilesListProgressState.setValue(ProgressState.IN_PROGRESS);

        executorService.submit(() -> {
            String content = getFilesListJson();
            String path="Empty";// = filesRepository.saveFile(FILES_LIST_FILENAME, content);
            byte[] byteArray = "1234567890".getBytes(StandardCharsets.UTF_8);

            try {
                 path = filesRepository.saveDocument(getApplication().getApplicationContext(), byteArray, "Abc.ptg",".ptg").toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            savedFilePath.postValue(path);

            if (path != null) {
                saveFilesListProgressState.postValue(ProgressState.DONE);
            } else {
                saveFilesListProgressState.postValue(ProgressState.ERROR);
            }
        });

        return savedFilePath;
    }

    public void sortFiles(FilesSortOption option) {
        executorService.submit(() -> {
            List<FileItem> currentFiles = files.getValue();

            if (currentFiles != null) {
                sortFiles(currentFiles, option);
                this.files.postValue(currentFiles);
            }
        });
    }

    private void sortFiles(List<FileItem> files, FilesSortOption option) {
        if (files == null) {
            return;
        }

        Comparator<FileItem> comparator = null;

        switch (option) {
            case FILENAME_ASC:
                comparator = (first, second) -> first.getFileModel().getFilename()
                        .compareTo(second.getFileModel().getFilename());
                break;

            case CREATION_TIME_ASC:
                comparator = (first, second) -> Long.compare(
                        first.getFileModel().getAttributes().getCreationTime(),
                        second.getFileModel().getAttributes().getCreationTime()
                );
                break;

            case EXTENSION_ASC:
                comparator = (first, second) -> first.getFileModel().getAttributes().getExtension()
                        .compareTo(second.getFileModel().getAttributes().getExtension());
                break;
        }

        Collections.sort(files, comparator);
    }

    private void triggerNumberOfMatchesNotification(int number) {
        String text = getApplication().getString(R.string.number_of_matches_d, number);
        String channel = getApplication().getString(R.string.channel_number_of_matches);

        int id = 1;

        Utils.triggerNotification(getApplication(), id, text, R.drawable.ic_highlight_black_24dp,
                null, channel, channel);
    }

    private Spannable createSpannable(String text, int color, int start, int end) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;
    }

    private String getFilesListJson() {
        List<FileItem> currentFiles = files.getValue();

        if (currentFiles != null) {
            JSONArray jsonArray = new JSONArray();

            for (FileItem file : currentFiles) {
                jsonArray.put(file.getFileModel().toJson());
            }

            return jsonArray.toString();
        } else {
            return "";
        }
    }
}