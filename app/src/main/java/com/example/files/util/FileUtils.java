package com.example.files.util;

import android.webkit.MimeTypeMap;

import com.example.files.model.FileAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtils {

    public static FileAttributes getFileAttributes(File file) {
        FileAttributes attributes = new FileAttributes();
        /*
         * Seems like Android does not support a time stamp to indicate the time of last modification.
         */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                Path path = Paths.get(file.toURI());
                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);

                attributes.setCreationTime(attrs.creationTime().toMillis());
                attributes.setLastModifiedTime(attrs.lastModifiedTime().toMillis());
                attributes.setSize(attrs.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                attributes.setCreationTime(file.lastModified());
                attributes.setLastModifiedTime(file.lastModified());
                attributes.setSize(file.length());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        attributes.setExtension(MimeTypeMap.getFileExtensionFromUrl(file.toString()));

        return attributes;
    }

    public static String guessFileMimeType(String fileName) {

        if (!fileName.contains(".")) {
            throw new IllegalArgumentException("parameter $fileName is invalidate, Must have And Only one '.'");
        }
        if (fileName.split(".").length > 2) {
            throw new IllegalArgumentException("parameter $fileName is invalidate, Only one '.' can exist");
        }
        int indexOfDot = fileName.lastIndexOf(".");
        String extension = fileName.substring(indexOfDot + 1);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }




}
