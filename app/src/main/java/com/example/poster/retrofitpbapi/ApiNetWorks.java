package com.example.poster.retrofitpbapi;

import com.example.poster.retrofitpbapi.models.ExchangeModel;
import com.example.poster.retrofitpbapi.models.PrivatOtdelModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiNetWorks {
    @GET("p24api/pubinfo?json&exchange")
    Call<List<ExchangeModel>> getExchangeRate(@Query("coursid") int coursId);

    /*@GET("p24api/pboffice?json&city=")
    Call<List<PrivatOtdelModel>> getPrivatOtdel(@Query())*/
}

