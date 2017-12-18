package com.example.poster.retrofitpbapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpDataHandler {
    public HttpDataHandler() {
    }

    public String getHTTPData(String requestURL){
        URL url;
        String responce = "";
        try {
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null){
                    responce += line;
                }
            }else {
                responce = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responce;
    }
}
