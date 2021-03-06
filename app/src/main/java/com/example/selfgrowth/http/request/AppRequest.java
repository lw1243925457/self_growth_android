package com.example.selfgrowth.http.request;

import com.example.selfgrowth.http.api.AppApi;
import com.example.selfgrowth.model.ApiResponse;
import com.example.selfgrowth.model.Feedback;

import java.util.function.Consumer;

import retrofit2.Call;

public class AppRequest extends Request {

    public void feedback(Feedback feedback, Consumer<? super Object> success, Consumer<? super Object> failed) {
        AppApi request = retrofit.create(AppApi.class);
        Call<ApiResponse> call = request.feedback(feedback);
        sendRequest(call, success, failed);
    }

    public void versionCheck(int versionCode, Consumer<? super Object> success, Consumer<? super Object> failed) {
        AppApi request = retrofit.create(AppApi.class);
        Call<ApiResponse> call = request.versionCheck(versionCode);
        sendRequest(call, success, failed);
    }
}
