package com.example.poster.retrofitpbapi;


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.poster.retrofitpbapi.models.ExchangeModel;

import static com.example.poster.retrofitpbapi.WorkWithMaps.MY_PERMISSIONS_REQUEST_LOCATION;


public class MainActivity extends AppCompatActivity{
    public TextView resultOfExchange;
    public TextView titleCurrContainer;
    private Button showCurrencyExchanger;
    private Button showMap;
    private Button resultShowBtn;
    private Spinner baseCurrSpinner;
    private Spinner secondCurrSpinner;
    private LinearLayout currencyExchangerContainer;
    private EditText currCount;

    private ProgressBar mProgresBar;
    public String[] titlesForCcu;
    private int counter = 0;
    private ArrayAdapter<String> spinnerAdapter;
    private FragmentManager manager = getSupportFragmentManager();
    private int couterMapVisible = 0;
    private LinearLayout mapContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLocationPermission();
        innitWidgets();
        new LoadDataFromPrivatBank().execute();

        showCurrencyExchanger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter++;
                checkCurrContainerVisible(counter);
                mapContainer.setVisibility(View.GONE);
            }
        });

        resultShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateData()){
                    resultOfExchange.setVisibility(View.VISIBLE);
                    resultOfExchange.setText("Стоимость валюти: " +
                            calculateCurrency(baseCurrSpinner.getSelectedItemPosition(),
                                    secondCurrSpinner.getSelectedItemPosition(),
                                    Double.valueOf(currCount.getText().toString())));
                }
            }
        });
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Network().getPrivatOtdel();
                couterMapVisible++;
                checkMapVisible(couterMapVisible);
            }
        });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getParent(),
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    private void checkMapVisible(int couterMapVisible) {
        if (couterMapVisible % 2 == 1){
            mapContainer.setVisibility(View.VISIBLE);
            manager.beginTransaction().replace(R.id.container, new WorkWithMaps()).commit();
        }else{
            mapContainer.setVisibility(View.GONE);
        }
    }

    private boolean validateData() {
        if (Double.valueOf(currCount.getText().toString()) >= 0.001){
            return true;
        }else {
            Toast.makeText(getBaseContext(), "Значение не должно бить менее 0.001", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private String calculateCurrency(int basePosition, int secPosition, double count){
        double result = 0;
        if (basePosition == secPosition){
            result = count;
        }else {
            result = checkBaseCcu(basePosition, secPosition) * count;
        }
        return String.format("%.4f", result) + " " + titlesForCcu[secPosition];
    }

    private double checkBaseCcu(int basePosition, int secPosition){
        String buyCcuForUSD = "";
        double result = 0;
        if (titlesForCcu[basePosition].equals("BTC") || titlesForCcu[secPosition].equals("BTC")){
            for (ExchangeModel model: Network.test) {
                if (model.getCcy().equals("USD")){
                    buyCcuForUSD = model.getBuy();
                    break;
                }
            }
            if (titlesForCcu[basePosition].equals("BTC") && !titlesForCcu[secPosition].equals("UAH")){
                result = Double.valueOf(Network.test.get(basePosition).getBuy()) *
                        Double.valueOf(buyCcuForUSD) / Double.valueOf(Network.test.get(secPosition).getBuy());
            }else {
                if (!titlesForCcu[secPosition].equals("UAH")){
                    result = Double.valueOf(Network.test.get(basePosition).getBuy()) /
                            (Double.valueOf(buyCcuForUSD) * Double.valueOf(Network.test.get(secPosition).getBuy()));
                }else {
                    result = (Double.valueOf(buyCcuForUSD) * Double.valueOf(Network.test.get(basePosition).getBuy()));
                }
            }
        }else {
            if (titlesForCcu[basePosition].equals("UAH") || titlesForCcu[secPosition].equals("UAH")){
                if (titlesForCcu[basePosition].equals("UAH")){
                    result = 1 / Double.valueOf(Network.test.get(secPosition).getBuy());
                }else {
                    if (titlesForCcu[secPosition].equals("BTC")){
                        result = Double.valueOf(Network.test.get(basePosition).getBuy()) * Double.valueOf(buyCcuForUSD);
                    }else {
                        result = Double.valueOf(Network.test.get(basePosition).getBuy());
                    }
                }
            }else {
                result = Double.valueOf(Network.test.get(basePosition).getBuy()) /
                        Double.valueOf(Network.test.get(secPosition).getBuy());
            }
        }
        return result;
    }

    private void innitDataToSpinners(){
        spinnerAdapter = new ArrayAdapter<>(getBaseContext(),
                android.R.layout.simple_spinner_item, titlesForCcu);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        baseCurrSpinner.setAdapter(spinnerAdapter);
        secondCurrSpinner.setAdapter(spinnerAdapter);
    }

    private void showExchangePrices() {
        for (ExchangeModel e: Network.test) {
            String tempText = titleCurrContainer.getText().toString();
            titleCurrContainer.setText(tempText + e.getCcy() + ": " + e.getBuy() + "; " + '\t' +
                    e.getSale() + " "  + '\t' + e.getBaseCcy() + ";" + '\n');
        }
    }

    private void getTitlesForCcu() {
        String defaultCcu = "UAH";
        titlesForCcu = new String[Network.test.size() + 1];
        for (int i = 0; i < titlesForCcu.length; i++) {
            if ( i == Network.test.size()){
                titlesForCcu[i] = defaultCcu;
            }else {
                titlesForCcu[i] = Network.test.get(i).getCcy();
            }
        }
    }

    private void checkCurrContainerVisible(int value) {
        if (value % 2 != 0){
            currencyExchangerContainer.setVisibility(View.VISIBLE);
        }else {
            currencyExchangerContainer.setVisibility(View.GONE);
        }
    }

    private void innitWidgets() {
        resultOfExchange = (TextView) findViewById(R.id.resultText);
        resultOfExchange.setVisibility(View.INVISIBLE);
        titleCurrContainer = (TextView) findViewById(R.id.currencyContainerTextView);
        titleCurrContainer.setVisibility(View.GONE);
        baseCurrSpinner = (Spinner) findViewById(R.id.baseCurrencySpinner);
        secondCurrSpinner = (Spinner) findViewById(R.id.convertCurrencySpinner);
        showCurrencyExchanger = (Button) findViewById(R.id.showCurrencyExchanger);
        resultShowBtn = (Button) findViewById(R.id.resultShowBtn);
        showMap = (Button) findViewById(R.id.showMap);
        currencyExchangerContainer = (LinearLayout) findViewById(R.id.currencyExchangerContainer);
        currencyExchangerContainer.setVisibility(View.GONE);
        mProgresBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgresBar.setVisibility(View.GONE);
        currCount = (EditText) findViewById(R.id.countEditText);
        mapContainer = (LinearLayout) findViewById(R.id.container);
        mapContainer.setVisibility(View.GONE);
    }

    public class LoadDataFromPrivatBank extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new Network().getExchangeRate();
            mProgresBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (true){
                if (!(Network.test.size() == 0)) break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgresBar.setVisibility(View.GONE);
            titleCurrContainer.setVisibility(View.VISIBLE);
            showExchangePrices();
            getTitlesForCcu();
            innitDataToSpinners();
        }
    }
}