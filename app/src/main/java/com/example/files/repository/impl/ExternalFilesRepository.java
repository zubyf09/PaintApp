package com.example.files.repository.impl;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.files.model.FileModel;
import com.example.files.repository.FilesRepository;
import com.example.files.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class ExternalFilesRepository implements FilesRepository {

    private List<FileModel> files = new ArrayList<>();


    String EXTERNAL_FILE_DIRECTORY = "PaintApp";
    private Uri DOCUMENT_EXTERNAL_URI = MediaStore.Files.getContentUri("external");


    /**
     * @return the list of files from the external storage.
     */
    @Override
    public List<FileModel> getAllFiles() {
        files.clear();

        URI entryUri = Environment.getExternalStorageDirectory().toURI();
        getAllFiles(entryUri , ".ptg");

        getFileInfo("/storage/emulated/0/MyPaint/App/sample.ptg");
        Log.e("Test ",getUniqueDocumentUri("Zubair","test"));
        return files;
    }

    // Then we need to read a list of all ".ptg" files
    // from that folder, including all in subfolders.
    // We save the Uri, the subfolder and the file name
    // for each of those files in a List (ArrayList<Painting>).
    // We save the info for each file like Uri, subfolder and name in a class we named "Painting".

    //  This list will be used to display the paintings
    //  in the gallery (we have the arrayAdapter ready that uses
    //  ArrayList<Painting>). We need the Uri to create InputStreams to
    //  read file data like previews and other info. We can already read
    //  the file content for a given Uri and read all our data.

    private void getAllFiles(URI uri, String extension) {

        File directory = new File(uri);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getPath().endsWith(extension)) {
                    this.files.add(new FileModel(
                            file.getName(),
                            file.getPath(),
                            FileUtils.getFileAttributes(file)
                    ));
                } else {
                    getAllFiles(file.toURI(),extension);
                }
            }
        }
    }

    //But we need a way to read if a file/directory, that corresponds to a
    //given uri, exists, is readable, if it is a file or directory and reading
    //the length of the file is also needed. Currently we do that via DocumentFile
    //but would prefer another way if possible.

    FileModel getFileInfo(String uri){

        File file = new File(uri);
        if(file.exists()){
            return new FileModel(
                    file.getName(),
                    file.getPath(),
                    FileUtils.getFileAttributes(file)
            );
        }
        return  null;
    }

    public String createFileWithFolders(String folder, String subFolder, String filename, String content) {
        String externalStorageDir = Environment.getExternalStorageDirectory().toString();
        externalStorageDir = externalStorageDir+File.separator+folder+subFolder;
        File file = new File(externalStorageDir);

        if(!file.isDirectory()){
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    //And a function that takes a default name String and a subfolder
    // String and returns a String with a name for a file that does not yet
    // exist in the given directory. This corresponds to the code that
    // I shared in my first email called getUniqueDocumentUri,
    // but it really just needs to return the name String, not a Uri.


    public String getUniqueDocumentUri(String parentFolder , String subFolder){

        String externalStorageDir = Environment.getExternalStorageDirectory().toString();
        externalStorageDir = externalStorageDir+File.separator+parentFolder+File.separator+subFolder;

        File file = new File(externalStorageDir);
        if(!file.isDirectory()){
            file.mkdirs();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return  createFile(file,"files.ptg" );
    }

    /**
     * Creates a new file and saves it to the external storage directory.
     * @param filename a filename with an extension.
     * @param content string data.
     * @return the absolute path of the saved file or null if an error occurred.
     */
    @Override
    public String saveFile(String filename, String content) {
//        String externalStorageDir = Environment.getExternalStorageDirectory().toString();
//
//        externalStorageDir = externalStorageDir+File.separator+"MyPaint/App";
//        File file = new File(externalStorageDir);
//
//        if(!file.isDirectory()){
//            file.mkdirs();
//        }
//        return createFile(file,filename);
        return "";

    }

    String createFile(File folder, String fileName)  {
        fileName = getTimeStamp()+fileName;
        final File file  = new File(folder, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return file.getAbsolutePath();
    }

    String getTimeStamp() {
        Long tsLong = System.currentTimeMillis()/1000;
        return  tsLong.toString();
    }

    @Override
    public Uri saveDocument(
            Context context, byte[] byteArray, String fileName, String mimeType) throws IOException {


        String docMimeType = mimeType;
        if(docMimeType ==null)
            docMimeType= FileUtils.guessFileMimeType(fileName);

        Uri docExternalUri =DOCUMENT_EXTERNAL_URI;

        if (isExternalStorageLegacy()) {
            // /storage/0/Document
            String externalDocDir =  Environment.getExternalStorageDirectory().toURI().toString();
            // /storage/emulated/0/Document/imooc
            File externalDocAppDir = new File(externalDocDir, EXTERNAL_FILE_DIRECTORY);
            if (!externalDocAppDir.exists()) {
                externalDocAppDir.mkdirs();
            }
            File docFile = new File(externalDocAppDir, fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(docFile);
                outputStream.write(byteArray);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DATA, docFile.getAbsolutePath());
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, docMimeType);
            return context.getContentResolver().insert(docExternalUri, values);
        } else {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.Files.FileColumns.MIME_TYPE, docMimeType);
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);

            Uri uri = context.getContentResolver().insert(DOCUMENT_EXTERNAL_URI, values);
            OutputStream openOutputStream = context.getContentResolver().openOutputStream(uri);
            openOutputStream.write(byteArray);
            openOutputStream.flush();
            openOutputStream.close();
            values.clear();
            values.put(MediaStore.MediaColumns.IS_PENDING, 0);
            context.getContentResolver().update(uri, values, null, null);
            grantUriPermission(context, uri);
            return uri;
        }
    }

    private boolean isExternalStorageLegacy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // return true:未开启
            return Environment.isExternalStorageLegacy();
        }
        return true;
    }

    private void grantUriPermission(Context activity, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.grantUriPermission(
                    getPackageName(activity),
                    uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            );
        }
    }

    public static String getPackageName(Context activity) {
        String packageName = null;
        try {
            packageName = String.valueOf(activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0).packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageName;
    }



}