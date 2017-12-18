package com.example.poster.retrofitpbapi;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by POSTER on 07.06.2017.
 */

public class WorkWithMaps extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LatLng curPos;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Permission was granted, do your thing!
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mapa_otdelov, container, false);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        try {
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            try{
                if (location != null){
                    curPos = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }catch (Exception e){
                Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }catch (Exception e){
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null){
            mMap.addMarker(new MarkerOptions().position(curPos).title("Я сейчас здесь :)")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 12.2f));
        }

        if (Network.otdelModels.size() != 0){
            new WorkWithMaps.GetCoordinates().execute();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class GetCoordinates extends AsyncTask<Void, Void, List<String>>{
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Pls, wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> responseArray = new ArrayList<>();
            try{
                for (int i = 0; i < Network.otdelModels.size(); i++) {
                    String address = (Network.otdelModels.get(i).getCity() + "+" +
                            Network.otdelModels.get(i).getIndex() + "+" + Network.otdelModels.get(i).getAddress()).replace(" ", "+");
                    HttpDataHandler http = new HttpDataHandler();
                    String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s", address);
                    responseArray.add(http.getHTTPData(url));
                }
                return responseArray;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<String> responseArray) {
            for (int i = 0; i < responseArray.size(); i++) {
                try {
                    JSONObject jsonObject = new JSONObject(responseArray.get(i));
                    String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lat").toString();
                    String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                            .getJSONObject("location").get("lng").toString();

                    mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(lat), Double.valueOf(lng)))
                            .title(Network.otdelModels.get(i).getName())
                            .snippet("телефон : " + Network.otdelModels.get(i).getPhone() + ";"));
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

