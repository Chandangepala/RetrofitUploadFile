package com.cyens.retrofituploadfile.viewmodels;

import androidx.lifecycle.ViewModel;

import com.cyens.retrofituploadfile.models.FileUploadResponse;
import com.cyens.retrofituploadfile.repositories.FileRepository;

import okhttp3.MultipartBody;
import java.io.File;
import java.io.IOException;

public class FileViewModel extends ViewModel {

    private final FileRepository fileRepository;

    public FileViewModel() {
        this.fileRepository = new FileRepository();
    }

    public FileViewModel(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileUploadResponse uploadFile(MultipartBody.Part file) {
        return fileRepository.uploadFile(file);
    }

    public File downloadFile(String fileName, String destinationPath) {
        try {
            return fileRepository.downloadFile(fileName, destinationPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
