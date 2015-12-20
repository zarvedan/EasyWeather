package com.zarvedan.easyweather.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.datas.InfosMeteo;
import com.zarvedan.easyweather.datas.VariablesGlobales;
import com.zarvedan.easyweather.gps.GPS;
import com.zarvedan.easyweather.ui.adapter.AdapterPrevisions;
import com.zarvedan.easyweather.ui.OnSwipeTouchListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    public static int ACTION_LISTEVILLES = 1;
    public VariablesGlobales mVariablesGlobales = new VariablesGlobales(this);
    public static GPS mGPS;

    public View vueAccueil;

    private static TextView temp;
    private static TextView minTemp;
    private static TextView maxTemp;
    private static TextView ville;
    private static TextView humidite;
    private static ImageView image;
    private static TextView previsions;
    private static TextView date;
    private static String dateStr;
    public ArrayList<InfosMeteo> listPrevisionsInfosMeteo = new ArrayList<InfosMeteo>();
    public AdapterPrevisions adapter;
    private static ListView listViewPrevisions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (mGPS == null) {
            mGPS = new GPS();
        }

        mGPS.recupererDonneesGps(this);
        initSwipe();
        // on récupère tous les éléments de notre UI pour pouvoir les mettre à jour
        temp = (TextView) findViewById(R.id.temp_text);
        minTemp = (TextView) findViewById(R.id.min_text);
        maxTemp = (TextView) findViewById(R.id.max_text);
        ville = (TextView) findViewById(R.id.ville);
        humidite = (TextView) findViewById(R.id.humidite_text);
        image = (ImageView) findViewById(R.id.image);
        previsions = (TextView) findViewById(R.id.previsions);
//        date = (TextView) findViewById(R.id.date);
//        dateStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        listViewPrevisions = (ListView) findViewById(R.id.liste_previsions);

    }

    public void updateListView(ArrayList<InfosMeteo> listPrevisionsInfosMeteo) {
        adapter = new AdapterPrevisions(MainActivity.this, listPrevisionsInfosMeteo);
        listViewPrevisions.setAdapter(adapter);
    }

    // appel de la mise à jour via l'UI
    public void recupererDonneesGpsUpdate(View view) {
        mGPS.recupererDonneesGps(view.getContext());
    }

    public void initSwipe() {
        vueAccueil = findViewById(R.id.accueil_vue);
        vueAccueil.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
            }

            @Override
            public void onSwipeLeft() {
                Intent secondeActivite = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(secondeActivite, ACTION_LISTEVILLES);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void rechercheVille(View view) {
        Intent secondeActivite = new Intent(MainActivity.this, AddCityActivity.class);
        startActivityForResult(secondeActivite, ACTION_LISTEVILLES);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ACTION_LISTEVILLES) {
            String result = data.getStringExtra("result");
            mGPS.loadDatasWithCityName(result);

        } else {
            Log.d("MainActivity", "aucun result code");
        }
    }

    public void updateCompleteDisplay(ArrayList<InfosMeteo> listPrevisionsInfosMeteo, String cityName) {
        temp.setText(listPrevisionsInfosMeteo.get(0).tempStr);
        minTemp.setText(listPrevisionsInfosMeteo.get(0).minTempStr + " °C");
        maxTemp.setText(listPrevisionsInfosMeteo.get(0).maxTempStr + " °C");
        ville.setText(listPrevisionsInfosMeteo.get(0).cityStr);
        humidite.setText(listPrevisionsInfosMeteo.get(0).humStr + " %");
        image.setImageDrawable(listPrevisionsInfosMeteo.get(0).picDrawableResized);
        previsions.setText("Prévisions pour "+cityName);
        ville.setText(cityName);
        updateListView(listPrevisionsInfosMeteo);
    }
//    public void updateTodayDisplay(InfosMeteo mInfosMeteo) {
//
//        temp.setText(mInfosMeteo.tempStr);
//        minTemp.setText(mInfosMeteo.minTempStr + " °C");
//        maxTemp.setText(mInfosMeteo.maxTempStr + " °C");
//        ville.setText(mInfosMeteo.cityStr);
//        humidite.setText(mInfosMeteo.humStr + " %");
//        image.setImageDrawable(mInfosMeteo.picDrawableResized);
//    }
}