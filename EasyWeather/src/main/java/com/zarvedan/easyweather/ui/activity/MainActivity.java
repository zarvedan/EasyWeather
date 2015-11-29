package com.zarvedan.easyweather.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.gps.GPS;
import com.zarvedan.easyweather.ui.OnSwipeTouchListener;


public class MainActivity extends Activity {

    public static int LISTEVILLES = 1;

    public GPS mGPS;


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

    }

    // appel de la mise à jour via l'UI
    public void recupererDonneesGpsUpdate(View view) {
        mGPS.recupererDonneesGps(view.getContext());
    }

    public void initSwipe() {
        View vueAccueil;
        vueAccueil = findViewById(R.id.accueil_vue);

        vueAccueil.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
            }

            @Override
            public void onSwipeLeft() {
                Intent secondeActivite = new Intent(MainActivity.this, AddCityActivity.class);
                startActivityForResult(secondeActivite, LISTEVILLES);
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
        startActivityForResult(secondeActivite, LISTEVILLES);
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == LISTEVILLES) {
            String result = data.getStringExtra("result");
            mGPS.loadDatasWithCityName(result);
        }
       else{
            Log.d("MainActivity", "aucun result code");
        }

    }


}