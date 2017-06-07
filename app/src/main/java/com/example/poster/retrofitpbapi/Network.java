package com.example.poster.retrofitpbapi;


import com.example.poster.retrofitpbapi.models.ExchangeModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by POSTER on 29.05.2017.
 */

public class Network {
    private ApiNetWorks apiNetWorks;
    public static List<ExchangeModel> test = new ArrayList<>();

    public Network(){
        apiNetWorks = new RetrofitConfig().getApiNetWorks();
    }

    public void getExchangeRate(){
        Call<List<ExchangeModel>> call = apiNetWorks.getExchangeRate(5);
        call.enqueue(new Callback<List<ExchangeModel>>() {
            @Override
            public void onResponse(Call<List<ExchangeModel>> call, Response<List<ExchangeModel>> response) {
                test = response.body();
            }

            @Override
            public void onFailure(Call<List<ExchangeModel>> call, Throwable t) {

            }
        });
    }
}
