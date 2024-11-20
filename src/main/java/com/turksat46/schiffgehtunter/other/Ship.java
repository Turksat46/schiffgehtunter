package com.turksat46.schiffgehtunter.other;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ship {

    private String name;
    public List <int[]> location;
    //private img
    private int groesse;



    public  Ship(String name, int groesse ){
        this.name = name;
        this.groesse = groesse;
        location = new  ArrayList<>();
    }

    public void addLocation(int x, int y){
        if (location.size() < groesse) {
            location.add(new int[]{x, y});
        }
        else {
            System.out.println("Schiff hat bereits maximalgröße");
        }
    }

    public int[][] getLocations(){
        int[][] result = new int[location.size()][2];
           for (int i = 0; i < location.size(); i++) {
               result[i] = location.get(i);
           }
        return result;
    }

    public String getLocationsString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < location.size(); i++) {
            int[] loc = location.get(i);
            sb.append("{").append(loc[0]).append(", ").append(loc[1]).append("}");
            if (i < location.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public String getName (){

        return name;
    }

    public int getGroesse (){
        return groesse;
    }
}
