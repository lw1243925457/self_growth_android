package com.example.selfgrowth.http.request;

import com.example.selfgrowth.http.api.UserApi;
import com.example.selfgrowth.model.ApiResponse;
import com.example.selfgrowth.model.LoginUser;

import java.util.function.Consumer;

import retrofit2.Call;

public class UserRequest extends Request {

    public void login(String name, String pwd, Consumer<? super Object> success, Consumer<? super Object> failed) {
        UserApi request = retrofit.create(UserApi.class);
        Call<ApiResponse> call = request.login(name, pwd);
        sendRequest(call, success, failed);
    }
}
