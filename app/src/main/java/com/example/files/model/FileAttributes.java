package com.example.files.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class FileAttributes implements Parcelable {

    private long creationTime;
    private long lastModifiedTime;
    private String extension;
    private long size;

    public FileAttributes() { }

    private FileAttributes(Parcel in) {
        creationTime = in.readLong();
        lastModifiedTime = in.readLong();
        extension = in.readString();
        size = in.readLong();
    }

    public static final Creator<FileAttributes> CREATOR = new Creator<FileAttributes>() {
        @Override
        public FileAttributes createFromParcel(Parcel in) {
            return new FileAttributes(in);
        }

        @Override
        public FileAttributes[] newArray(int size) {
            return new FileAttributes[size];
        }
    };

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("creationTime", creationTime);
            json.put("lastModifiedTime", lastModifiedTime);
            json.put("extension", extension);
            json.put("size", size);
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
        dest.writeLong(creationTime);
        dest.writeLong(lastModifiedTime);
        dest.writeString(extension);
        dest.writeLong(size);
    }
}