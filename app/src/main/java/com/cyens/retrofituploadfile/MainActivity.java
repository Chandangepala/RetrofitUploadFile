package com.cyens.retrofituploadfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.cyens.retrofituploadfile.models.FileUploadResponse;
import com.cyens.retrofituploadfile.viewmodels.FileViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnUpload, btnDownload;
    private ImageView imgUpload;
    private FileViewModel fileViewModel;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initUI();

        fileViewModel = new ViewModelProvider(this).get(FileViewModel.class);

        imgUpload.setOnClickListener(v -> {
            openImagePicker();
        });

        btnUpload.setOnClickListener(v -> {

            File file = new File(getApplicationContext().getFilesDir(), "image.jpg");

            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
                FileOutputStream outputStream = new FileOutputStream(file);

                // Using a buffer for efficient copying
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

                new Thread(() -> {
                    try {

                        FileUploadResponse response = fileViewModel.uploadFile(part);

                        Log.e("ManinActivity", "Upload Response: " + response);

                        // Switch back to the main thread to update the UI (show the Toast)
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getBaseContext(), "Response: " + response, Toast.LENGTH_SHORT).show());

                    } catch (Exception e) {
                        Log.e("ManinActivity", "Upload Error: " + e.getMessage());
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(getBaseContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private void initUI(){
        btnUpload = findViewById(R.id.btn_upload);
        btnDownload = findViewById(R.id.btn_download);
        imgUpload = findViewById(R.id.img_upload);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imgUpload.setImageURI(imageUri);
        }
    }

}