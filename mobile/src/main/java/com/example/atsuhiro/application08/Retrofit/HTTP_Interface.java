package com.example.atsuhiro.application08.Retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface HTTP_Interface {
    //    @FormUrlEncoded
//    formやと怒られる！！！
    @POST("./exec")
    Call<PostBody> createRequest(@Body PostBody pBody);

    // TODO　GETの処理っている？

}
