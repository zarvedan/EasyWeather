package com.zarvedan.easyweather.datas.BDD;

/**
 * Created by andre on 28/06/14.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VillesBDD {

    private static final int VERSION_BDD = 1;
    private static final String NOM_BDD = "villes.db";

    private static final String TABLE_VILLES = "table_villes";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_NOM = "NOM";
    private static final int NUM_COL_NOM = 1;

    private SQLiteDatabase bdd;

    private BaseSQLite maBaseSQLite;


    public VillesBDD(Context context) {
        //On créer la BDD et sa table
        maBaseSQLite = new BaseSQLite(context, NOM_BDD, null, VERSION_BDD);

    }

    public void open() {
        //on ouvre la BDD en écriture
        
        bdd = maBaseSQLite.getWritableDatabase();
    }

    public void close() {
        //on ferme l'accès à la BDD
        bdd.close();
    }

    public SQLiteDatabase getBDD() {
        return bdd;
    }

    public long insertVille(Ville ville) {
        //Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = new ContentValues();
        //on lui ajoute une valeur associé à une clé (qui est le nom de la colonne dans laquelle on veut mettre la valeur)
        values.put(COL_NOM, ville.getNom());
        //on insère l'objet dans la BDD via le ContentValues
        return bdd.insert(TABLE_VILLES, null, values);
    }

    public int updateVille(int id, Ville ville) {

        ContentValues values = new ContentValues();
        values.put(COL_NOM, ville.getNom());

        return bdd.update(TABLE_VILLES, values, COL_ID + " = " + id, null);
    }

    public int removeVilleWithID(int id) {
        //Suppression d'une ville de la BDD grâce à l'ID
        return bdd.delete(TABLE_VILLES, COL_ID + " = " + id, null);
    }

    public int removeVilleWithName(String nom) {
        //Suppression d'une ville de la BDD grâce à son nom
        return bdd.delete(TABLE_VILLES, COL_NOM + " = " + nom, null);
    }


    public Ville getVilleWithID(int id){
        //Récupère dans un Cursor les valeur correspondant à une ville contenue dans la BDD
        Cursor c = bdd.query(TABLE_VILLES, new String[] {COL_ID, COL_NOM}, COL_ID + " LIKE \"" + id +"\"", null, null, null, null);
        return cursorToVille(c);
    }

    public Ville getVilleWithNom(String nom){
        //Récupère dans un Cursor les valeur correspondant à une ville contenue dans la BDD
        Cursor c = bdd.query(TABLE_VILLES, new String[] {COL_ID, COL_NOM}, COL_NOM + " LIKE \"" + nom +"\"", null, null, null, null);
        return cursorToVille(c);
    }


    //Cette méthode permet de convertir un cursor en une ville
    private Ville cursorToVille(Cursor c) {
        //si aucun élément n'a été retourné dans la requête, on renvoie null
        if (c.getCount() == 0)
            return null;

        c.moveToFirst();
        Ville ville = new Ville();
        ville.setId(c.getInt(NUM_COL_ID));
        ville.setNom(c.getString(NUM_COL_NOM));
        c.close();

        return ville;
    }

    /**
     * Created by andre on 28/06/14.
     */
    public static class Ville {

            private int id;
            private String nom;


            public Ville(){}

            public Ville(String nom){
                this.nom = nom;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getNom() {
                return nom;
            }

            public void setNom(String nom) {
                this.nom = nom;
            }

            public String toString(){
                return "ID : "+id+"\nNOM : "+nom;
            }

    }
}

