package com.example.poster.retrofitpbapi;


import android.widget.Toast;

import com.example.poster.retrofitpbapi.models.ExchangeModel;
import com.example.poster.retrofitpbapi.models.PrivatOtdelModel;

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
    public static List<PrivatOtdelModel> otdelModels = new ArrayList<>();

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

    public void getPrivatOtdel(){
        Call<List<PrivatOtdelModel>> call = apiNetWorks.getPrivatOtdel("Соборна");
        call.enqueue(new Callback<List<PrivatOtdelModel>>() {
            @Override
            public void onResponse(Call<List<PrivatOtdelModel>> call, Response<List<PrivatOtdelModel>> response) {
                otdelModels = response.body();
                for (PrivatOtdelModel model: otdelModels) {
                    System.out.println(model.toString());
                }
            }

            @Override
            public void onFailure(Call<List<PrivatOtdelModel>> call, Throwable t) {
                System.out.println("Error to load map");
            }
        });
    }
}
