package com.cyens.retrofituploadfile.repositories;

import com.cyens.retrofituploadfile.data.UploadService;
import com.cyens.retrofituploadfile.models.FileUploadResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response; // Import for Retrofit Response

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileRepository {


    public FileUploadResponse uploadFile(MultipartBody.Part file) {
        try {
            Response<FileUploadResponse> response = UploadService.Instance.get().uploadFile(file).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            } else {
                String errorMessage = "Failed to upload: Error Code " + response.code();
                if (response.errorBody() != null) {
                    try {
                        errorMessage += " - " + response.errorBody().string();
                    } catch (IOException e) {
                        // Ignored
                    }
                }
                return new FileUploadResponse("", errorMessage, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new FileUploadResponse("", "Failed to upload: IOException: " + e.getMessage(), false);
        } catch (Exception e) {
            e.printStackTrace();
            return new FileUploadResponse("", "Failed to upload: Exception: " + e.getMessage(), false);
        }
    }

    public File downloadFile(String fileName, String destinationPath) throws IOException {
        File file = new File(destinationPath);

        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Could not create parent directories: " + parentDir.getPath());
            }
        }

        Response<ResponseBody> response;
        try {
            response = UploadService.Instance.get().downloadFile(fileName).execute(); // Synchronous call
        } catch (Exception e) {
            throw new IOException("Failed to execute download request: " + e.getMessage(), e);
        }

        if (!response.isSuccessful() || response.body() == null) {
            String errorDetails = "";
            if (response.errorBody() != null) {
                try {
                    errorDetails = response.errorBody().string();
                } catch (IOException e) {
                    // ignore
                }
            }
            throw new IOException("Download failed: HTTP " + response.code() + " - " + response.message() + " " + errorDetails);
        }

        try (InputStream inputStream = response.body().byteStream();
             OutputStream outputStream = new FileOutputStream(file)) {

            byte[] buffer = new byte[8192]; // DEFAULT_BUFFER_SIZE is often 8KB
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            return file;

        } catch (IOException e) {
            // If an error occurs, try to delete the partially downloaded file
            if (file.exists()) {
                file.delete();
            }
            throw new IOException("Failed to save downloaded file: " + e.getMessage(), e);
        } catch (Exception e) {
            if (file.exists()) {
                file.delete();
            }
            throw new IOException("An unexpected error occurred during file download: " + e.getMessage(), e);
        }
    }
}
