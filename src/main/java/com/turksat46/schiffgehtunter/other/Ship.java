package com.turksat46.schiffgehtunter.other;

public class Ship {

    private String name;
   // private int location;
    //private img
    private int groesse;

    public Ship(String name, int groesse ){
        this.name = name;
        this.groesse = groesse;
    }

    public String getName (){

        return name;
    }

    public int getGroesse (){
        return groesse;
    }
}
