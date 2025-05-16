package com.cyens.retrofituploadfile.data;

import com.cyens.retrofituploadfile.models.FileUploadResponse;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface UploadService {

    @Multipart
    @POST("upload")
    Call<FileUploadResponse> uploadFile(@Part MultipartBody.Part file);

    @Streaming
    @GET("download/{fileName}")
    Call<ResponseBody> downloadFile(@Path("fileName") String fileName);

    class Instance {
        private static UploadService instance;

        public static UploadService get() {
            if (instance == null) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build();

                instance = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:8080/api/files/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(client)
                        .build()
                        .create(UploadService.class);
            }
            return instance;
        }
    }
}
