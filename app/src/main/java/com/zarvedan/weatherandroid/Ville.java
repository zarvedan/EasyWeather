package com.zarvedan.weatherandroid;

/**
 * Created by andre on 28/06/14.
 */
public class Ville {

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
