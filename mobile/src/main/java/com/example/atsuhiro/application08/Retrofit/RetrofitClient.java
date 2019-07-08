package com.example.atsuhiro.application08.Retrofit;

import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static HTTP_Interface apiService;
    private PostBody body = new PostBody("testName");
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    // Constructor
    public RetrofitClient(String baseUrl) {
        Log.d("Interface","Create Interface");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(HTTP_Interface.class);
    }

    public void setURL(String URL){
        Log.d("Interface","ReCreate Interface");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(HTTP_Interface.class);
    }

    public void post(String name,String order, long[] action){
        body.setBody(name,order,action);
        Call<PostBody> call = apiService.createRequest(body);
        call.enqueue(new Callback<PostBody>() {
            @Override
            public void onResponse(Call<PostBody> call, retrofit2.Response<PostBody> response) {
                Log.d("Client","onResponse");
            }

            @Override
            public void onFailure(Call<PostBody> call, Throwable t) {
                Log.d("Client","onFailure");
                Log.d("Client",t.getMessage());
            }
        });
    }
}
