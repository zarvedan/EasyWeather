package com.zarvedan.easyweather.gps;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.datas.InfosMeteo;
import com.zarvedan.easyweather.datas.VariablesGlobales;
import com.zarvedan.easyweather.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andre on 29/11/15.
 */
public class GPS {

    public Context mContext;
    public Activity mActivity;
    public RequestQueue queue;


    public static JSONObject mObjTemp = null;
    public static JSONArray mArrTemp = null;

    public static JsonObjectRequest jsObjRequest;

    public static ArrayList<InfosMeteo> mListInfosMeteo = new ArrayList<InfosMeteo>();

    public void recupererDonneesGps(Context contextRecu) {
        this.mContext = contextRecu;
        this.mActivity = (MainActivity) contextRecu;
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) mContext.getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String provider1 = locationManager.getBestProvider(criteria, true);
        try {
            Location l = locationManager.getLastKnownLocation(provider1);

            locationManager.requestLocationUpdates(provider1, 2000, 10, locationListener);

            loadDatasWithCoord(l);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //  updateWithNewLocation(location);
            // on ne met rien pour économiser la batterie
            // on bouton update est prévu
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int Status, Bundle extras) {
        }
    };


    ///////////////////////////////////////////////////////////////////////////////////
    //
    // Mise à jour des données météo après sélection d'une de ses villes favorites
    //
    /////////////////////////////////////////////////////////////////////////////////

    public void loadDatasWithCityName(String villeStr) {
        String villeStrModif = villeStr.replace(" ", "-");
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q=" + villeStrModif + "&mode=json&APPID=" + VariablesGlobales.APIKEY + "&units=metric&cnt=" + VariablesGlobales.JOURS_PREVISIONS;
        queue = Volley.newRequestQueue(mContext);

        jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateDatas(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Erreur [" + error + "]");
                Toast.makeText(mContext, "Problème serveur ou ville inexistante", Toast.LENGTH_LONG).show();
            }
        }
        );

        queue.add(jsObjRequest);
    }

    public void updateDatas(JSONObject response) {

        try {
            mArrTemp = response.getJSONArray("list");
            mListInfosMeteo.clear();
            for (int i = 0; i < VariablesGlobales.JOURS_PREVISIONS; i++) {
                mObjTemp = (JSONObject) mArrTemp.get(i);
                mListInfosMeteo.add(new InfosMeteo(mObjTemp));
            }
            ((MainActivity) mActivity).updateCompleteDisplay(mListInfosMeteo, response.getJSONObject("city").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //
    // Mise à jour des données météo suivant la géolocalisation du téléphone
    //
    /////////////////////////////////////////////////////////////////////////////////

    public void loadDatasWithCoord(Location l) {
        if (l != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");

            String latStr = df.format(lat);
            String lonStr = df.format(lon);

            String url = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latStr + "&lon=" + lonStr + "&APPID=" + VariablesGlobales.APIKEY + "&units=metric&cnt=" + VariablesGlobales.JOURS_PREVISIONS;

            queue = Volley.newRequestQueue(mContext);

            jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            updateDatas(response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("Erreur [" + error + "]");
                    final TextView ville = (TextView) mActivity.findViewById(R.id.ville);
                    ville.setText("Problème d'accès au serveur");
                }
            }
            );
            queue.add(jsObjRequest);
        }
    }

}
