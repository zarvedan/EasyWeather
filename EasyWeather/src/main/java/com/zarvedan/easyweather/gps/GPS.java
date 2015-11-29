package com.zarvedan.easyweather.gps;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.datas.VariablesGlobales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andre on 29/11/15.
 */
public class GPS {

    public VariablesGlobales mVariablesGlobales;
    public Context mContext;
    public Activity mActivity;


    private TextView temp;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView ville;
    private TextView pression;
    private TextView humidite;
    private TextView description;
    private ImageView image;
    private TextView date;

    private String cityStr ;
    private String tempStr ;
    private String minTempStr ;
    private String maxTempStr ;
    private String pressStr ;
    private String humStr ;
    private String descrStr;
    private String iconStr ;
    private  String dateStr;




    public void recupererDonneesGps(Context contextRecu) {
        this.mContext = contextRecu;
        this.mActivity = (Activity) contextRecu;
        mVariablesGlobales = new VariablesGlobales();
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
        Location l = locationManager.getLastKnownLocation(provider1);

        locationManager.requestLocationUpdates(provider1, 2000, 10, locationListener);

        loadDatasWithCoord(l);
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

        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + villeStrModif + ",fr&APPID=" + mVariablesGlobales.getAPIKEY() + "&units=metric";
        RequestQueue queue = Volley.newRequestQueue(mContext);

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        updateDatas(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Erreur [" + error + "]");
                //final TextView ville = (TextView) findViewById(R.id.ville);
                //ville.setText("Problème d'accès au serveur");
                Toast.makeText(mContext, "Problème serveur ou ville inexistante", Toast.LENGTH_LONG).show();
            }
        }
        );

        queue.add(jsObjRequest);
    }

    public void updateDatas(JSONObject response) {

        // on récupère tous les éléments de notre UI pour pouvoir les mettre à jour
        temp = (TextView) mActivity.findViewById(R.id.temp_text);
        minTemp = (TextView) mActivity.findViewById(R.id.min_text);
        maxTemp = (TextView) mActivity.findViewById(R.id.max_text);
        ville = (TextView) mActivity.findViewById(R.id.ville);
        pression = (TextView) mActivity.findViewById(R.id.pression_text);
        humidite = (TextView) mActivity.findViewById(R.id.humidite_text);
        description = (TextView) mActivity.findViewById(R.id.description);
        image = (ImageView) mActivity.findViewById(R.id.image);
        date = (TextView) mActivity.findViewById(R.id.date);


        dateStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        JSONObject ObjTemp = null;
        JSONArray ArrTemp = null;

        try {

            cityStr = response.getString("name");
            ArrTemp = response.getJSONArray("weather");

            iconStr = "pic" + ArrTemp.getJSONObject(0).getString("icon");
            if (iconStr.charAt(iconStr.length() - 1) == 'n') {
                iconStr = iconStr.replace('n', 'd');
            }

            Log.w("Main", "IconStr: " + iconStr);
            descrStr = ArrTemp.getJSONObject(0).getString("description");

            ObjTemp = response.getJSONObject("main");
            tempStr = ObjTemp.getString("temp");
            minTempStr = ObjTemp.getString("temp_min");
            maxTempStr = ObjTemp.getString("temp_max");
            pressStr = ObjTemp.getString("pressure");
            humStr = ObjTemp.getString("humidity");


            Log.w("Main", "CATCH response vaut:" + response.toString());

        } catch (JSONException e) {
            Log.w("Main", "CATCH response dans exception vaut:" + response.toString());

        }

        Double i = Double.parseDouble(tempStr);
        Integer j = (int) Math.round(i);
        temp.setText(j.toString() + " °C");
        minTemp.setText(minTempStr + " °C");
        maxTemp.setText(maxTempStr + " °C");
        ville.setText(cityStr);
        description.setText(descrStr);
        humidite.setText(humStr + " %");
        pression.setText(pressStr + " hPa");
        date.setText(dateStr);

        // on récupère le drawable intitulé comme iconStr pour le mettre à jour notre ImageView
        int id = mContext.getResources().getIdentifier(iconStr, "drawable", mContext.getPackageName());


        // On redimensionne l'icone à afficher
        Drawable picDrawable = mContext.getResources().getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable) picDrawable).getBitmap();
        Drawable picDrawableResized = new BitmapDrawable(mContext.getResources(), Bitmap.createScaledBitmap(bitmap, 300, 300, true));
        image.setImageDrawable(picDrawableResized);

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

            String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latStr + "&lon=" + lonStr + "&APPID=" + mVariablesGlobales.getAPIKEY() + "&units=metric";
            Log.w("Main", url);
            RequestQueue queue = Volley.newRequestQueue(mContext);

            final JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
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
