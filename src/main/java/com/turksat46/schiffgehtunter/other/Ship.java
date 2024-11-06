package com.turksat46.schiffgehtunter.other;


import java.util.ArrayList;
import java.util.List;

public class Ship {

    private String name;
    List<Integer> location;
    //private img
    private int groesse;



    public Ship(String name, int groesse ){
        this.name = name;
        this.groesse = groesse;
        location = List.of(0,0);
    }

    public void setLocation(int x, int y, int x1, int y1){
        //location.set(0,{x,y});
    }

    public String getName (){

        return name;
    }

    public int getGroesse (){
        return groesse;
    }
}
