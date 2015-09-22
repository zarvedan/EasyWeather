package com.zarvedan.weatherandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    //public int CHOOSE_BUTTON_REQUEST = 0;
    public String APIKEY = "5bbb23f51cfe9aae50b517c67a421cad";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recupererDonneesGps();
        initSwipe();

    }


    public void initSwipe() {
        View maVue;
        maVue = findViewById(R.id.maVue1);

        maVue.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeLeft() {
                Intent secondeActivite = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(secondeActivite,1);
            }

        });
    }

    // appel de la mise à jour via l'UI
    public void recupererDonneesGpsUpdate(View view) {
        recupererDonneesGps();
    }

    public void recupererDonneesGps() {
        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);

        String provider1 = locationManager.getBestProvider(criteria, true);
        Location l = locationManager.getLastKnownLocation(provider1);

        //  updateWithNewLocation(l);

        locationManager.requestLocationUpdates(provider1, 2000, 10, locationListener);

        loadDatasWithCoord(l);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //  updateWithNewLocation(location);
            // on ne met rien pour économiser les ressources et la batterie du téléphone :)
            // on bouton update est prévu
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int Status, Bundle extras) {
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void rechercheVille(View view) {
        Intent secondeActivite = new Intent(MainActivity.this, AddCityActivity.class);
        startActivityForResult(secondeActivite, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Toast.makeText(MainActivity.this, "onactivity request code: " + requestCode + " et result code " + resultCode, Toast.LENGTH_SHORT).show();

        if (resultCode == 1) {
            String result = data.getStringExtra("result");
            //Toast.makeText(MainActivity.this, "onactivity result is : " + result, Toast.LENGTH_SHORT).show();
            loadDatasWithCityName(result);

        }
        if (resultCode != 1) {
            // Toast.makeText(MainActivity.this, "result code != 1 " + resultCode, Toast.LENGTH_SHORT).show();
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

            String latStr= df.format(lat);
            String lonStr = df.format(lon);

            String url ="http://api.openweathermap.org/data/2.5/weather?lat=" + latStr + "&lon=" + lonStr+"&APPID="+APIKEY+"&units=metric";
            Log.w("Main", url);
            RequestQueue queue = Volley.newRequestQueue(this);

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
                    final TextView ville = (TextView) findViewById(R.id.ville);
                    ville.setText("Problème d'accès au serveur");
                }
            }
            );

            queue.add(jsObjRequest);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //
    // Mise à jour des données météo après sélection d'une de ses villes favorites
    //
    /////////////////////////////////////////////////////////////////////////////////

    public void loadDatasWithCityName(String villeStr) {
        String villeStrModif = villeStr.replace(" ","-");

        String url ="http://api.openweathermap.org/data/2.5/weather?q="+villeStrModif+",fr&APPID="+APIKEY+"&units=metric";
        RequestQueue queue = Volley.newRequestQueue(this);

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
                Toast.makeText(getBaseContext(), "Problème serveur ou ville inexistante", Toast.LENGTH_LONG).show();
            }
        }
        );

        queue.add(jsObjRequest);
    }

    public void updateDatas(JSONObject response) {

        // on récupère tous les éléments de notre UI pour pouvoir les mettre à jour
        final TextView temp = (TextView) findViewById(R.id.temp_text);
        final TextView minTemp = (TextView) findViewById(R.id.min_text);
        final TextView maxTemp = (TextView) findViewById(R.id.max_text);
        final TextView ville = (TextView) findViewById(R.id.ville);
        final TextView pression = (TextView) findViewById(R.id.pression_text);
        final TextView humidite = (TextView) findViewById(R.id.humidite_text);
        final TextView description = (TextView) findViewById(R.id.description);
        final ImageView image = (ImageView) findViewById(R.id.image);
        final TextView date = (TextView) findViewById(R.id.date);

        String cityStr = null;
        String tempStr = null;
        String minTempStr = null;
        String maxTempStr = null;
        String pressStr = null;
        String humStr = null;
        String descrStr = null;
        String iconStr = null;
        String dateStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

        JSONObject ObjTemp = null;
        JSONArray ArrTemp = null;

        try {

            cityStr = response.getString("name");
            ArrTemp = response.getJSONArray("weather");

            iconStr = "pic" + ArrTemp.getJSONObject(0).getString("icon");
            if(iconStr.charAt(iconStr.length()-1) == 'n'){
                iconStr = iconStr.replace('n','d');
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
        int id = getResources().getIdentifier(iconStr, "drawable", getPackageName());


        // On redimensionne l'icone à afficher
        Drawable picDrawable = getResources().getDrawable(id);
        Bitmap bitmap = ((BitmapDrawable) picDrawable).getBitmap();
        Drawable picDrawableResized = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 300, 300, true));
        image.setImageDrawable(picDrawableResized);

    }

}