package com.zarvedan.easyweather.datas;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.ui.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andre on 01/12/15.
 */
public class InfosMeteo {

    public String cityStr = "";
    public String jourSemaine = "";
    public String jourMois = "";
    public String tempStr;
    public String minTempStr;
    public String maxTempStr;
    public String humStr;
    public String iconStr = "";

    public Drawable picDrawable;
    public Bitmap bitmap;
    public Drawable picDrawableResized;


    public InfosMeteo(JSONObject json) {
        try {
            this.iconStr = "pic" + json.getJSONArray("weather").getJSONObject(0).getString("icon");
            if (iconStr.charAt(iconStr.length() - 1) == 'n') {
                iconStr = iconStr.replace('n', 'd');
            }
            int id = VariablesGlobales.mContext.getResources().getIdentifier(iconStr, "drawable", VariablesGlobales.mContext.getPackageName());
            try {
                this.picDrawable = VariablesGlobales.mContext.getResources().getDrawable(id);
            } catch (Exception e) {
                id = R.drawable.picna;
                this.picDrawable = VariablesGlobales.mContext.getResources().getDrawable(id);
            }


            if ((this.picDrawable) != null) {
                this.bitmap = ((BitmapDrawable) this.picDrawable).getBitmap();
            }
            this.picDrawableResized = new BitmapDrawable(VariablesGlobales.mContext.getResources(), Bitmap.createScaledBitmap(this.bitmap, 70, 70, true));
            Date date = new Date();
            date.setTime(Long.parseLong(json.getString("dt")) * 1000);

            SimpleDateFormat newDateFormat = new SimpleDateFormat("EEEE");
            this.jourSemaine = newDateFormat.format(date).toUpperCase();
            newDateFormat = new SimpleDateFormat("d/MM");
            this.jourMois = newDateFormat.format(date).toUpperCase();
            BigDecimal bg = new BigDecimal(json.getJSONObject("temp").getString("day")).setScale(0, RoundingMode.HALF_UP);
            this.tempStr = bg.toString() + " °C";
this.cityStr= "e";
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}




