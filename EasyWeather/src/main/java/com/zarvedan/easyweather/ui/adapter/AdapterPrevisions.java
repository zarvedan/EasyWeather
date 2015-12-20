package com.zarvedan.easyweather.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.datas.InfosMeteo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 05/12/15.
 */
public class AdapterPrevisions extends BaseAdapter{

    private List<InfosMeteo> listInfosMeteo;

    private final Context context;

    public AdapterPrevisions(Context context, ArrayList<InfosMeteo> listInfosMeteo) {
        super();
        this.context = context;
        this.listInfosMeteo = listInfosMeteo;
    }

    @Override
    public int getCount() {
        return listInfosMeteo.size();
    }

    @Override
    public Object getItem(int position) {

        return listInfosMeteo.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.previsions, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.image_prev);
        TextView jourSemaineView = (TextView) convertView.findViewById(R.id.jour_semaine);
        TextView jourMoisView = (TextView) convertView.findViewById(R.id.jour_mois);
        TextView tempview  = (TextView) convertView.findViewById(R.id.temp_prev);

        int id = context.getResources().getIdentifier(listInfosMeteo.get(position).iconStr, "drawable", context.getPackageName());
        try {
            listInfosMeteo.get(position).picDrawable = context.getResources().getDrawable(id);
        } catch (Exception e) {
            id = R.drawable.picna;
            listInfosMeteo.get(position).picDrawable = context.getResources().getDrawable(id);
        }
        listInfosMeteo.get(position).bitmap = ((BitmapDrawable) listInfosMeteo.get(position).picDrawable).getBitmap();
        listInfosMeteo.get(position).picDrawableResized = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(listInfosMeteo.get(position).bitmap, 70, 70, true));
        iconView.setImageDrawable(listInfosMeteo.get(position).picDrawableResized);


        jourSemaineView.setText(listInfosMeteo.get(position).jourSemaine);
        jourMoisView.setText(listInfosMeteo.get(position).jourMois);
        tempview.setText(listInfosMeteo.get(position).tempStr);
        return convertView;
    }
}
