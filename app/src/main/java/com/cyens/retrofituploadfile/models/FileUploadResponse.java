package com.cyens.retrofituploadfile.models;

public class FileUploadResponse {
    String fileName;
    String message;
    Boolean success;

    public FileUploadResponse(String fileName, String message, Boolean success) {
        this.fileName = fileName;
        this.message = message;
        this.success = success;
    }
}
