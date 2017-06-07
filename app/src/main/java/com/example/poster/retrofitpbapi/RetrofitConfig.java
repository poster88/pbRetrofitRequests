package com.example.poster.retrofitpbapi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitConfig {
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.privatbank.ua")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private ApiNetWorks apiNetWorks = retrofit.create(ApiNetWorks.class);

    public ApiNetWorks getApiNetWorks(){
        return apiNetWorks;
    }
}
