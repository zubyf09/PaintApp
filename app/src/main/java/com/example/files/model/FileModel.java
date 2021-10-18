package com.example.files.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class FileModel implements Parcelable {

    private String filename;
    private String path;
    private FileAttributes attributes;

    public FileModel(String filename, String path, FileAttributes attributes) {
        this.filename = filename;
        this.path = path;
        this.attributes = attributes;
    }

    private FileModel(Parcel in) {
        filename = in.readString();
        path = in.readString();
        attributes = in.readParcelable(FileAttributes.class.getClassLoader());
    }

    public static final Creator<FileModel> CREATOR = new Creator<FileModel>() {
        @Override
        public FileModel createFromParcel(Parcel in) {
            return new FileModel(in);
        }

        @Override
        public FileModel[] newArray(int size) {
            return new FileModel[size];
        }
    };

    public String getFilename() {
        return filename;
    }

    public String getPath() {
        return path;
    }

    public FileAttributes getAttributes() {
        return attributes;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("filename", filename);
            json.put("path", path);
            json.put("attributes", attributes.toJson());
        } catch (JSONException e) {
            return null;
        }

        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filename);
        dest.writeString(path);
        dest.writeParcelable(attributes, flags);
    }
}