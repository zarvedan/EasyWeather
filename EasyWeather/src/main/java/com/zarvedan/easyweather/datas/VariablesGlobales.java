package com.zarvedan.easyweather.datas;

import android.content.Context;

import com.zarvedan.easyweather.ui.activity.MainActivity;

/**
 * Created by andre on 29/11/15.
 */
public class VariablesGlobales {


    //Clé api.openweathermap.org
    public static String APIKEY = "5bbb23f51cfe9aae50b517c67a421cad";
    public static final int JOURS_PREVISIONS = 15;
    public static Context mContext;

    public VariablesGlobales(MainActivity mainActivity) {
        if (mContext == null) {
            mContext = mainActivity;
        }
    }
}
