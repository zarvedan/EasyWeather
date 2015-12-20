package com.zarvedan.easyweather.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zarvedan.easyweather.R;
import com.zarvedan.easyweather.datas.BDD.VillesBDD;
import com.zarvedan.easyweather.ui.OnSwipeTouchListener;

import java.util.ArrayList;


/**
 * Created by andre on 06/05/14.
 */
public class AddCityActivity extends Activity {
    public AlertDialog.Builder dialogBoxNewCity;
    public AlertDialog dialogBoxNewCityVariable;
    public AlertDialog.Builder dialogBoxCityToDelete;
    public ArrayList<String> listVilles;
    public ArrayAdapter<String> adapter;
    public VillesBDD villeBdd = new VillesBDD(this);
    public SQLiteDatabase bdd;

    public ListView lv;
    public VillesBDD.Ville villeTmp = new VillesBDD.Ville();
    public View alertDialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initSwipe();

        lv = (ListView) findViewById(R.id.liste_villes);
        listVilles = new ArrayList<String>();
        adapter = new ArrayAdapter(AddCityActivity.this, android.R.layout.simple_list_item_1, listVilles);

        villeBdd.open();

        bdd = villeBdd.getBDD();
        long dbSize = DatabaseUtils.queryNumEntries(bdd, "table_villes");
        int dbSizeInt = (int) dbSize;
        int i = 0;
        int nbVilles = 0;

        while (nbVilles < dbSizeInt) {
            if (villeBdd.getVilleWithID(i) != null) {
                VillesBDD.Ville villeTmpFromBdd = villeBdd.getVilleWithID(i);
                listVilles.add(villeTmpFromBdd.getNom());
                nbVilles++;
                i++;
            } else {
                i++;
            }
        }

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String s = (String) (lv.getItemAtPosition(arg2));

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", s);
                setResult(MainActivity.ACTION_LISTEVILLES, returnIntent);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // arg 2 est le numero de ligne dans la listview
                initAlertDialogLongClick(arg2);
                dialogBoxCityToDelete.show();
                return true;
            }
        });

    }

    public void ajouterVille(View v) {

        initAlertDialogSimpleClick();
        dialogBoxNewCityVariable = dialogBoxNewCity.create();
        dialogBoxNewCityVariable.show();
    }


    public void retourMainActivity(View v) {
        finish();
    }

    public void initAlertDialogSimpleClick() {

        dialogBoxNewCity = new AlertDialog.Builder(this);

        //On instancie notre layout en tant que View
        LayoutInflater factory = LayoutInflater.from(this);
        alertDialogView = factory.inflate(R.layout.alert_dialog, null);

        //On affecte la vue personnalisé que l'on a crée à notre AlertDialog
        dialogBoxNewCity.setView(alertDialogView);

        //On donne un titre à l'AlertDialog
        dialogBoxNewCity.setTitle(R.string.ajouter_ville);

        //On modifie l'icône de l'AlertDialog pour le fun ;)
        dialogBoxNewCity.setIcon(android.R.drawable.ic_input_add);

        //On affecte un bouton "OK" à notre AlertDialog et on lui affecte un évènement
        dialogBoxNewCity.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                //Lorsque l'on cliquera sur le bouton "OK", on récupère l'EditText correspondant à notre vue personnalisée (cad à alertDialogView)
                EditText et = (EditText) alertDialogView.findViewById(R.id.EditText1);
                if (!et.getText().toString().isEmpty()) {
                    VillesBDD.Ville villeTmp = new VillesBDD.Ville(et.getText().toString());
                    villeBdd.insertVille(villeTmp);
                    updateListFromDB();
                } else {
                    Toast.makeText(getBaseContext(), "Vous n'avez rien rentré...", Toast.LENGTH_LONG).show();
                }
                dialogBoxNewCityVariable.cancel();
            }
        });

        //On crée un bouton "Annuler" à notre AlertDialog et on lui affecte un évènement
        dialogBoxNewCity.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //Lorsque l'on cliquera sur annuler on quittera l'application

            }
        });
    }

    public void initAlertDialogLongClick(final int id) {
        villeBdd.open();
        lv = (ListView) findViewById(R.id.liste_villes);
        String s = (String) (lv.getItemAtPosition(id));
        villeTmp = villeBdd.getVilleWithNom(s);
        if (villeTmp.getNom() != null) {
            final String nom = villeTmp.getNom();
            dialogBoxCityToDelete = new AlertDialog.Builder(this);
            dialogBoxCityToDelete.setIcon(android.R.drawable.ic_delete);
            dialogBoxCityToDelete.setTitle("Voulez-vous supprimer " + nom + " ?");
            dialogBoxCityToDelete.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    villeBdd.removeVilleWithID(villeTmp.getId());

                    updateListFromDB();

                }
            });

            dialogBoxCityToDelete.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getBaseContext(), " a supprimer en bdd", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void initSwipe() {
        View maVue;
        maVue = findViewById(R.id.maVue2);
        maVue.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeRight() {
                //Intent mainActivity = new Intent(AddCityActivity.this, MainActivity.class);
                //startActivity(mainActivity);
                finish();
            }

            @Override
            public void onSwipeLeft() {

            }

        });
    }

    public void updateListFromDB() {

        bdd = villeBdd.getBDD();
        long dbSize = DatabaseUtils.queryNumEntries(bdd, "table_villes");
        int dbSizeInt = (int) dbSize;
        int i = 1;
        int nbVilles = 0;
        listVilles.clear();

        while (nbVilles < dbSizeInt) {
            if (villeBdd.getVilleWithID(i) != null) {
                VillesBDD.Ville villeTmpFromBdd = villeBdd.getVilleWithID(i);
                listVilles.add(villeTmpFromBdd.getNom());
                nbVilles++;
                i++;
            } else {
                i++;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }
}